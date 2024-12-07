package com.bizzagi.daytrip.ui.Trip.Edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.CardDestinationBinding

class DestinationDragAdapter(
    private val dayId: String
) : RecyclerView.Adapter<DestinationDragAdapter.ViewHolder>() {
    private var destinations = mutableListOf<DataItem>()
    private var onItemLongClick: ((DataItem, View) -> Boolean)? = null

    class ViewHolder(val binding: CardDestinationBinding) : RecyclerView.ViewHolder(binding.root)

    fun setOnItemLongClickListener(listener: (DataItem, View) -> Boolean) {
        onItemLongClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardDestinationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = destinations[position]
        holder.binding.apply {
            tvDestinasi.text = destination.name
            tvAddress.text = destination.address

            root.setOnLongClickListener { view ->
                onItemLongClick?.invoke(destination, view) ?: false
            }
        }
    }

    override fun getItemCount() = destinations.size

    fun submitList(newList: List<DataItem>) {
        destinations.clear()
        destinations.addAll(newList)
        notifyDataSetChanged()
    }

    fun getCurrentList(): List<DataItem> = destinations.toList()
}