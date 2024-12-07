package com.bizzagi.daytrip.ui.Trip.Detail

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.CardDestinationBinding
import com.bizzagi.daytrip.ui.Maps.Details.DetailsMapsActivity

class DestinationAdapter : ListAdapter<DataItem,DestinationAdapter.DestinationViewHolder>(
    DIFF_CALLBACK) {
    class DestinationViewHolder(private val binding: CardDestinationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(destination: DataItem) {
            binding.apply {
                tvDestinasi.text = destination.name
                tvAddress.text = destination.address

                root.setOnClickListener {
                    val intent = Intent(root.context, DetailsMapsActivity::class.java).apply {
                        putExtra(DetailsMapsActivity.EXTRA_LAT, destination.latitude)
                        putExtra(DetailsMapsActivity.EXTRA_LONG, destination.longitude)
                        putExtra(DetailsMapsActivity.EXTRA_ID, destination.id)
                    }
                    root.context.startActivity(intent)
                }
            }
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