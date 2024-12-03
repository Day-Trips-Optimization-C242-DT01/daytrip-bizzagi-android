package com.bizzagi.daytrip.ui.Trip

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.data.retrofit.response.Plans.Plan
import kotlinx.coroutines.launch

class PlansViewModel(
    private val plansRepository: PlansRepository,
    private val destinationRepository: DestinationRepository
) : ViewModel() {
    private val _planIds = MutableLiveData<List<Plan>>()
    val planIds: LiveData<List<Plan>> get() = _planIds

    private val _days = MutableLiveData<Map<String, List<String>>>()
    val days: LiveData<Map<String, List<String>>> get() = _days

    //nanti fetch result buat destinations
    private val _destinations = MutableLiveData<List<DataItem>>()
    val destinations: LiveData<List<DataItem>> get() = _destinations

    fun fetchAllDestinations() {
        Log.d("PlansViewModel", "fetchAllDestinations called")
        viewModelScope.launch {
            val allDestinations = plansRepository.getPlansDestinations()
            Log.d("PlansViewModel", "Got ${allDestinations.size} destination IDs")
            if (allDestinations.isNotEmpty()) {
                val result = destinationRepository.getDestinations(allDestinations)
                when (result) {
                    is Result.Success -> {
                        Log.d("PlansViewModel", "Setting destinations with ${result.data.data.size} items")
                        _destinations.value = result.data.data
                    }
                    is Result.Error -> {
                        Log.e("PlansViewModel", "Error fetching all destinations: $result.error")
                        _destinations.value = emptyList()
                    }
                    is Result.Loading -> {
                        Log.d("PlansViewModel", "Fetching all destinations...")
                    }
                }
            } else {
                _destinations.value = emptyList()
            }
        }
    }
    fun fetchPlanIds() {
        viewModelScope.launch {
            _planIds.value = plansRepository.getPlanIds()
        }
    }

    fun fetchDays(planId: String) {
        viewModelScope.launch {
            _days.value = plansRepository.getDays(planId)
        }
    }

    fun fetchDestinationsForDay(planId: String, dayId: String) {
        viewModelScope.launch {
            val daysData = plansRepository.getDays(planId)
            val destinationIds = daysData[dayId] ?: emptyList()

            if (destinationIds.isNotEmpty()) {
                val result = destinationRepository.getDestinations(destinationIds)
                when (result) {
                    is Result.Success -> {
                        _destinations.value = result.data.data
                    }
                    is Result.Error -> {
                        Log.e("PlansViewModel", "Error fetching destinations: $result.error")
                        _destinations.value = emptyList()
                    }
                    is Result.Loading -> {

                        Log.d("PlansViewModel", "Fetching destinations...")
                    }
                }
            } else {
                _destinations.value = emptyList()
            }
        }
    }
}
