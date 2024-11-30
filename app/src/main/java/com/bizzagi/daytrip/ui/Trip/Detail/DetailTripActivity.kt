package com.bizzagi.daytrip.ui.Trip.Detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.databinding.ActivityDetailTripBinding
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DetailTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTripBinding

    private val viewModel by viewModels <PlansViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var daysAdapter: DayPagerAdapter
    private lateinit var destinationsAdapter: DestinationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getStringExtra("TRIP_ID") ?: run {
            Toast.makeText(this, "Plan ID error", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView(tripId)
        observeViewModel()

        viewModel.fetchDays(planId = tripId)
    }

    private fun setupRecyclerView(tripId: String) {
        daysAdapter = DayPagerAdapter { selectedDayIndex ->
            lifecycleScope.launch {
                Log.d("DetailTripActivity", "Selected day: $selectedDayIndex")
                viewModel.fetchDestinationsForDay(tripId, selectedDayIndex)
            }
        }

        binding.rvDay.apply {
            layoutManager = LinearLayoutManager(this@DetailTripActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = daysAdapter
        }

        destinationsAdapter = DestinationAdapter()

        binding.rvDestination.apply {
            layoutManager = LinearLayoutManager(this@DetailTripActivity)
            adapter = destinationsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.days.observe(this) { days ->
            if (days.isNotEmpty()) {
                val dayKeys = days.keys.toList()
                daysAdapter.submitList(dayKeys)

                val firstDay = dayKeys.first()
                val tripId = intent.getStringExtra("TRIP_ID") ?: return@observe
                viewModel.fetchDestinationsForDay(tripId, firstDay)
            }
        }

        viewModel.destinations.observe(this) { destinations ->
            if (destinations.isNotEmpty()) {
                destinationsAdapter.submitList(destinations)
            } else {
                Toast.makeText(this, "No destinations available for this day", Toast.LENGTH_SHORT).show()
            }
        }
    }


}