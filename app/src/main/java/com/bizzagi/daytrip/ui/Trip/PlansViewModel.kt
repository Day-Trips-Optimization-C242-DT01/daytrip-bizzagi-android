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
import kotlinx.coroutines.launch

class PlansViewModel(
    private val plansRepository: PlansRepository,
    private val destinationRepository: DestinationRepository
) : ViewModel() {
    private val _planIds = MutableLiveData<List<String>>()
    val planIds: LiveData<List<String>> get() = _planIds

    private val _days = MutableLiveData<Map<String, List<String>>>()
    val days: LiveData<Map<String, List<String>>> get() = _days

    private val _destinations = MutableLiveData<List<DataItem>>()
    val destinations: LiveData<List<DataItem>> get() = _destinations

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
                        _destinations.value = result.data.data // Update destinasi ke UI
                    }
                    is Result.Error -> {
                        Log.e("PlansViewModel", "Error fetching destinations: ${result.error}")
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
