package com.bizzagi.daytrip.ui.Trip.Detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.DestinationDummy
import com.bizzagi.daytrip.data.retrofit.response.PlansDummy
import com.bizzagi.daytrip.databinding.CardDestinationBinding
import com.bizzagi.daytrip.ui.Trip.TripAdapter

class DestinationAdapter : ListAdapter<DestinationDummy,DestinationAdapter.DestinationViewHolder>(DestinationAdapter.DiffCallback()) {
    class DestinationViewHolder(private val binding: CardDestinationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: DestinationDummy) {
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

    class DiffCallback : DiffUtil.ItemCallback<DestinationDummy>() {
        override fun areItemsTheSame(oldItem: DestinationDummy, newItem: DestinationDummy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DestinationDummy, newItem: DestinationDummy): Boolean {
            return oldItem == newItem
        }
    }

}