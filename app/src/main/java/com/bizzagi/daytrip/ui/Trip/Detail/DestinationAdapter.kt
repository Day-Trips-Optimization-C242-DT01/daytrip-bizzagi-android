package com.bizzagi.daytrip.ui.Trip.Detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.DestinationsData
import com.bizzagi.daytrip.databinding.CardDestinationBinding
import com.bizzagi.daytrip.ui.Trip.TripAdapter

class DestinationAdapter : ListAdapter<DestinationsData,DestinationAdapter.DestinationViewHolder>(
    DIFF_CALLBACK) {
    class DestinationViewHolder(private val binding: CardDestinationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: DestinationsData) {
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DestinationsData>() {
            override fun areItemsTheSame(
                oldItem: DestinationsData,
                newItem: DestinationsData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DestinationsData, newItem: DestinationsData): Boolean {
                return oldItem == newItem
            }
        }
    }
}