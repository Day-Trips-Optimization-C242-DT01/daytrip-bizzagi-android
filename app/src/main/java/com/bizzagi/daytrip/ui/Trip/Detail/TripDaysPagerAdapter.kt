package com.bizzagi.daytrip.ui.Trip.Detail

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

class TripDaysPagerAdapter : FragmentStateAdapter {
    private val startDate: String?
    private val endDate: String?

    // Constructor untuk Activity
    constructor(fragmentActivity: FragmentActivity, startDate: String?, endDate: String?) :
            super(fragmentActivity) {
        this.startDate = startDate
        this.endDate = endDate
    }

    // Constructor untuk Fragment
    constructor(fragment: Fragment, startDate: String?, endDate: String?) :
            super(fragment) {
        this.startDate = startDate
        this.endDate = endDate
    }

    override fun getItemCount(): Int {
        startDate?.let { start ->
            endDate?.let { end ->
                val startDateTime = LocalDate.parse(start)
                val endDateTime = LocalDate.parse(end)
                return ChronoUnit.DAYS.between(startDateTime, endDateTime).toInt() + 1
            }
        }
        return 0
    }

    override fun createFragment(position: Int): Fragment {
        val dayFragment = TripDayFragment()
        dayFragment.arguments = bundleOf("day" to position + 1)
        return dayFragment
    }
}
