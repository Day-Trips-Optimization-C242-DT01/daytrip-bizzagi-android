package com.bizzagi.daytrip.ui.Trip.Detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.databinding.CardDayBinding

class DayPagerAdapter(
    private val onDayClick: (String) -> Unit
) : ListAdapter<String, DayPagerAdapter.DayViewHolder>(DIFF_CALLBACK) {

    private var selectedPosition = 0

    inner class DayViewHolder(private val binding: CardDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dayIndex: String) {
            binding.tvHari.text = "Day ${dayIndex.replace("day", "")}"

            // Set click listener for the item
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dayId = getItem(position)
                    val oldPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)

                    onDayClick(dayId)
                }
            }

            // Update the visual state based on selection
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION && position == selectedPosition) {
                binding.root.setBackgroundResource(android.R.color.holo_blue_light)
                binding.tvHari.setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.white))
            } else {
                binding.root.setBackgroundResource(android.R.color.white)
                binding.tvHari.setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.black))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = CardDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}