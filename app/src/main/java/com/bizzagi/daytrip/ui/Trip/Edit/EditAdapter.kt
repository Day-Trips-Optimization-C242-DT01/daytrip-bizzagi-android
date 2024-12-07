package com.bizzagi.daytrip.ui.Trip.Edit

import android.content.ClipData
import android.graphics.Color
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.ItemEditPlanBinding

class EditAdapter : RecyclerView.Adapter<EditAdapter.DayViewHolder>() {
    private var daysList: List<Pair<String, List<DataItem>>> = emptyList()
    private var onDaysUpdated: ((Map<String, List<DataItem>>) -> Unit)? = null
    private lateinit var currentDragItem: DataItem
    private lateinit var sourceDayId: String
    private var recyclerView: RecyclerView? = null
    private var isDragging = false

    fun setOnDaysUpdatedListener(listener: (Map<String, List<DataItem>>) -> Unit) {
        onDaysUpdated = listener
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    inner class DayViewHolder(
        val binding: ItemEditPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var destinationAdapter: DestinationDragAdapter
        private lateinit var itemTouchHelper: ItemTouchHelper

        fun bind(day: Pair<String, List<DataItem>>) {
            val dayId = day.first
            binding.dayName.text = dayId

            destinationAdapter = DestinationDragAdapter(dayId)

            binding.innerRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = destinationAdapter
            }

            setupIntraDayDragAndDrop(dayId)

            setupInterDayDragAndDrop(dayId)

            destinationAdapter.submitList(day.second)
        }

        private fun setupIntraDayDragAndDrop(dayId: String) {
            val callback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.bindingAdapterPosition
                    val toPos = target.bindingAdapterPosition

                    if (fromPos == RecyclerView.NO_POSITION || toPos == RecyclerView.NO_POSITION) {
                        return false
                    }

                    val list = destinationAdapter.getCurrentList().toMutableList()
                    if (fromPos < list.size && toPos < list.size) {
                        val temp = list[fromPos]
                        list[fromPos] = list[toPos]
                        list[toPos] = temp
                        destinationAdapter.submitList(list)
                        notifyDaysUpdated()
                        return true
                    }
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }

            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(binding.innerRecyclerView)
        }

        private fun setupInterDayDragAndDrop(dayId: String) {
            binding.root.setOnDragListener { view, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        view.setBackgroundColor(Color.parseColor("#E0E0E0"))
                        true
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        view.setBackgroundColor(Color.parseColor("#B3E5FC"))
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        view.setBackgroundColor(Color.parseColor("#E0E0E0"))
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        val targetDayId = dayId
                        if (isDragging && sourceDayId != targetDayId) {
                            handleDragBetweenDays(sourceDayId, targetDayId)
                        }
                        isDragging = false
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        isDragging = false
                        true
                    }
                    else -> true
                }
            }

            destinationAdapter.setOnItemLongClickListener { item, itemView ->
                if (!isDragging) {
                    isDragging = true
                    sourceDayId = dayId
                    currentDragItem = item

                    val clipData = ClipData.newPlainText("destination", item.name)
                    val shadow = View.DragShadowBuilder(itemView)

                    ViewCompat.startDragAndDrop(
                        itemView,
                        clipData,
                        shadow,
                        null,
                        0
                    )
                    true
                } else false
            }
        }
    }

    private fun handleDragBetweenDays(sourceDayId: String, targetDayId: String) {
        val sourceAdapter = findAdapterByDay(sourceDayId)
        val targetAdapter = findAdapterByDay(targetDayId)

        if (sourceAdapter != null && targetAdapter != null) {
            val sourceList = sourceAdapter.getCurrentList().toMutableList()
            val targetList = targetAdapter.getCurrentList().toMutableList()

            if (sourceList.contains(currentDragItem)) {
                sourceList.remove(currentDragItem)

                if (sourceList.isNotEmpty()) {
                    targetList.add(currentDragItem)

                    sourceAdapter.submitList(sourceList)
                    targetAdapter.submitList(targetList)

                    notifyDaysUpdated()
                }
            }
        }
    }

    private fun notifyDaysUpdated() {
        val currentData = mutableMapOf<String, List<DataItem>>()
        daysList.forEach { (dayId, _) ->
            findAdapterByDay(dayId)?.getCurrentList()?.let { list ->
                currentData[dayId] = list
            }
        }
        onDaysUpdated?.invoke(currentData)
    }

    private fun findAdapterByDay(dayId: String): DestinationDragAdapter? {
        val position = daysList.indexOfFirst { it.first == dayId }
        if (position != -1) {
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? DayViewHolder
            return viewHolder?.binding?.innerRecyclerView?.adapter as? DestinationDragAdapter
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemEditPlanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(daysList[position])
    }

    override fun getItemCount() = daysList.size

    fun submitData(data: List<Pair<String, List<DataItem>>>) {
        daysList = data
        notifyDataSetChanged()
    }
}