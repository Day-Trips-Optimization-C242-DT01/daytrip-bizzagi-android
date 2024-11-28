package com.bizzagi.daytrip.ui.Trip.Detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.CardDestinationBinding

class DestinationAdapter : ListAdapter<DataItem,DestinationAdapter.DestinationViewHolder>(
    DIFF_CALLBACK) {
    class DestinationViewHolder(private val binding: CardDestinationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: DataItem) {
            binding.tvDestinasi.text = destination.name
            binding.tvAddress.text = destination.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = CardDestinationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}