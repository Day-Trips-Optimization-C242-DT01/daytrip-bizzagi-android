package com.bizzagi.daytrip.ui.Trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.DestinationsData
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class PlansViewModel(private val plansRepository: PlansRepository) : ViewModel() {

    private val _planIds = MutableLiveData<List<String>>()
    val planIds: LiveData<List<String>> get() = _planIds

    private val _days = MutableLiveData<List<String>>()
    val days: LiveData<List<String>> get() = _days

    private val _destinations = MutableLiveData<List<DestinationsData>>()
    val destinations : MutableLiveData<List<DestinationsData>> get() = _destinations

    fun fetchPlans() {
        _planIds.value = plansRepository.getPlanIds()
    }

    fun fetchDays(planId: String) {
        val response = plansRepository.getDays(planId)
        _days.value = response.data.days.keys.toList()
    }

    fun fetchDestinations(planId: String, day: String) {
        val destinations = plansRepository.getDestinationsByDay(planId, day)
        _destinations.value = destinations
    }
}
