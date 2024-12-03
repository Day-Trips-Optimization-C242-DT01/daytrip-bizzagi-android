package com.bizzagi.daytrip.ui.Trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.Plans.Plan
import com.bizzagi.daytrip.databinding.CardMyTripBinding

class TripAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<Plan, TripAdapter.TripViewHolder>(DiffCallback()) {

    class TripViewHolder(private val binding: CardMyTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: Plan, onItemClick: (String) -> Unit) {
            binding.tvPerjalanan.text = plan.planName
            binding.root.setOnClickListener {
                onItemClick(plan.id)
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

    class DiffCallback : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem == newItem
        }
    }
}
