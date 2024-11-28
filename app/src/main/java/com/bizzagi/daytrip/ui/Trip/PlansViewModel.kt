package com.bizzagi.daytrip.ui.Trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import kotlinx.coroutines.launch

class PlansViewModel(private val plansRepository: PlansRepository) : ViewModel() {

    private val _planIds = MutableLiveData<List<String>>()
    val planIds: LiveData<List<String>> get() = _planIds

    private val _days = MutableLiveData<List<String>>()
    val days: LiveData<List<String>> get() = _days

    private val _destinations = MutableLiveData<List<DataItem>>()
    val destinations : MutableLiveData<List<DataItem>> get() = _destinations

    fun fetchPlans() {
        _planIds.value = plansRepository.getPlanIds()
    }

    fun fetchDays(planId: String) {
        val response = plansRepository.getDays(planId)
        _days.value = response.data.days.keys.toList()
    }

    fun fetchDestinations(planId: String, day: String) {
        viewModelScope.launch {
            try {
                val destinations = plansRepository.getDestinationsByDay(planId, day)
                _destinations.value = destinations
            } catch (e: Exception) {
                // Handle error case
                _destinations.value = emptyList()
            }
        }
    }
}
