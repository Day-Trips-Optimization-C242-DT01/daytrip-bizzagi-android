package com.bizzagi.daytrip.ui.Trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.PlansDummy
import com.bizzagi.daytrip.databinding.CardMyTripBinding

class TripAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<PlansDummy, TripAdapter.TripViewHolder>(DiffCallback()) {

    class TripViewHolder(private val binding: CardMyTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: PlansDummy, onItemClick: (String) -> Unit) {
            binding.tvPerjalanan.text = trip.cohort
            binding.tvDate.text = "${trip.startDate} - ${trip.endDate}"

            // Set click listener for the item
            binding.root.setOnClickListener {
                onItemClick(trip.id) // Pass trip ID to the callback
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

    class DiffCallback : DiffUtil.ItemCallback<PlansDummy>() {
        override fun areItemsTheSame(oldItem: PlansDummy, newItem: PlansDummy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlansDummy, newItem: PlansDummy): Boolean {
            return oldItem == newItem
        }
    }
}