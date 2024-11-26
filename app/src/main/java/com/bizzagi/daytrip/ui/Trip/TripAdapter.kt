package com.bizzagi.daytrip.ui.Trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.databinding.CardMyTripBinding

class TripAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<String, TripAdapter.TripViewHolder>(DiffCallback()) {

    class TripViewHolder(private val binding: CardMyTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(planId: String, onItemClick: (String) -> Unit) {
            binding.tvPerjalanan.text = planId

            // Set click listener for the item
            binding.root.setOnClickListener {
                onItemClick(planId) // Pass the Plan ID to the callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = CardMyTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
