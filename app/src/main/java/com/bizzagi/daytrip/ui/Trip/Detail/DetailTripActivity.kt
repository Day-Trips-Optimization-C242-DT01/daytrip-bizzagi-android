package com.bizzagi.daytrip.ui.Trip.Detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.data.retrofit.ApiConfig
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.databinding.ActivityDetailTripBinding
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DetailTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTripBinding
    private lateinit var viewModel: PlansViewModel
    private lateinit var daysAdapter: DayPagerAdapter
    private lateinit var destinationsAdapter: DestinationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService()

        val repository = PlansRepository(DestinationRepository(apiService))

        val factory = ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(PlansViewModel::class.java)

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
                viewModel.fetchDestinations(tripId, selectedDayIndex)
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
            daysAdapter.submitList(days)
            if (days.isNotEmpty()) {
                val tripId = intent.getStringExtra("TRIP_ID")
                tripId?.let { id ->
                    lifecycleScope.launch {
                        viewModel.fetchDestinations(id, days[0])
                    }
                }
            }
        }

        viewModel.destinations.observe(this) { destinations ->
            Log.d("DetailTripActivity", "New destinations received: ${destinations.size}")
            destinationsAdapter.submitList(destinations)
        }
    }
}