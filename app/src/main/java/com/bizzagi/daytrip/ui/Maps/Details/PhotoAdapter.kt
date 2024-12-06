package com.bizzagi.daytrip.ui.Maps.Details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.databinding.PhotoItemLayoutBinding
import com.bumptech.glide.Glide

class PhotoAdapter : ListAdapter<String, PhotoAdapter.PhotoViewHolder>(DIFF_CALLBACK) {
    class PhotoViewHolder(private val binding: PhotoItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photoUrl: String) {
            Glide.with(binding.root.context)
                .load(photoUrl)
                .into(binding.ivPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}
