package com.bizzagi.daytrip.ui.Trip.Detail

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bizzagi.daytrip.ui.Trip.PlansViewModel

class TripDaysPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val viewModel: PlansViewModel
) : FragmentStateAdapter(fragmentActivity) {

    private var maxVisibleDays = 3 // Default visible days

    override fun getItemCount(): Int {
        // Only load up to maxVisibleDays or all days if less
        return viewModel.tripDays.value?.let { days ->
            minOf(days.size, maxVisibleDays)
        } ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        val dayFragment = TripDayFragment()
        dayFragment.arguments = bundleOf("day" to position + 1)
        return dayFragment
    }

    // Function to expand visible range when paged
    @SuppressLint("NotifyDataSetChanged")
    fun increaseVisibleDays() {
        viewModel.tripDays.value?.size?.let { totalDays ->
            if (maxVisibleDays < totalDays) {
                maxVisibleDays += 1 // Expand by 1 day
                notifyDataSetChanged()
            }
        }
    }
}