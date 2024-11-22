package com.bizzagi.daytrip.ui.Trip.Detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bizzagi.daytrip.data.retrofit.repository.PlansDummyRepository
import com.bizzagi.daytrip.databinding.ActivityDetailTripBinding
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class DetailTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTripBinding
    private lateinit var viewModel: PlansViewModel
    private lateinit var pagerAdapter: TripDaysPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = PlansDummyRepository
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PlansViewModel::class.java)

        val tripId = intent.getStringExtra("TRIP_ID") ?: return
        viewModel.initializeTrip(tripId)

        setupViewPagerAndTabs()
        observeTripData()
    }

    private fun setupViewPagerAndTabs() {
        pagerAdapter = TripDaysPagerAdapter(this, viewModel)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = viewModel.getFormattedDateForPosition(position)
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Expand visible range dynamically as user swipes
                if (position == pagerAdapter.itemCount - 1) {
                    pagerAdapter.increaseVisibleDays()
                }
            }
        })
    }

    private fun observeTripData() {
        // Update ViewPager when trip days are updated
        viewModel.tripDays.observe(this) {
            pagerAdapter.notifyDataSetChanged()
        }
    }
}