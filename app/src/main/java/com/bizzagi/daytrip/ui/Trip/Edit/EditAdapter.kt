package com.bizzagi.daytrip.ui.Trip.Edit

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.ItemEditPlanBinding
import com.bizzagi.daytrip.ui.Trip.Detail.DestinationAdapter

class EditAdapter : RecyclerView.Adapter<EditAdapter.DayViewHolder>() {
    private var daysList: List<Pair<String, List<DataItem>>> = emptyList()

    class DayViewHolder(
        private val binding: ItemEditPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val destinationAdapter = DestinationAdapter()

        init {
            binding.innerRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = destinationAdapter
            }
        }

        fun bind(day: Pair<String, List<DataItem>>) {
            Log.d("EditAdapter", "Binding day: ${day.first}, Destinations: ${day.second}")

            binding.dayName.text = day.first
            destinationAdapter.submitList(day.second)
        }
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

    override fun getItemCount(): Int = daysList.size

    fun submitData(data: List<Pair<String, List<DataItem>>>) {
        Log.d("EditAdapter", "Submitting data: $data")

        if (data.isEmpty() || data.any { it.first.isEmpty() }) {
            Log.w("EditAdapter", "Submitted data is invalid or empty")
        }

        daysList = data
        notifyDataSetChanged()
    }
}
