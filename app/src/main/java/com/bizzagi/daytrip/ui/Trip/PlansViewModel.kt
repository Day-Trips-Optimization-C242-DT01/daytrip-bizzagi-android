package com.bizzagi.daytrip.ui.Trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.retrofit.repository.PlansDummyRepository
import com.bizzagi.daytrip.data.retrofit.response.DestinationDummy
import com.bizzagi.daytrip.data.retrofit.response.PlansDummy
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class PlansViewModel (private val TripDummyRepository: PlansDummyRepository): ViewModel() {
    private val _selectedTrip = MutableLiveData<PlansDummy?>()
    val selectedTrip: MutableLiveData<PlansDummy?> = _selectedTrip

    private val _currentDayIndex = MutableLiveData<Int>()
    val currentDayIndex: LiveData<Int> = _currentDayIndex

    private val _tripDays = MutableLiveData<List<LocalDate>>()
    val tripDays: LiveData<List<LocalDate>> = _tripDays

    private val planRepository = PlansDummyRepository

    private val _allTrips = MutableLiveData<List<PlansDummy>>()
    val allTrips: LiveData<List<PlansDummy>> = _allTrips

    init {
        fetchTrips()
    }

    //ntar ubah livedata waktu fetch api + add result

    fun initializeTrip(tripId: String) {
        viewModelScope.launch {
            val trip = planRepository.getTrips().find { it.id == tripId }
            _selectedTrip.value = trip

            trip?.let {
                calculateTripDays(it.startDate, it.endDate)
            }
        }
    }

    private fun calculateTripDays(startDate: String, endDate: String) {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        val days = mutableListOf<LocalDate>()
        var currentDate = start

        while (!currentDate.isAfter(end)) {
            days.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        _tripDays.value = days
    }

    // Update current day index ketika tab berubah
    fun updateCurrentDay(position: Int) {
        _currentDayIndex.value = position
    }

    // Get formatted date string untuk tab title
    fun getFormattedDateForPosition(position: Int): String {
        return _tripDays.value?.get(position)?.let { date ->
            // Format: "Day 1 (Nov 11)"
            "Day ${position + 1} (${date.format(DateTimeFormatter.ofPattern("MMM dd"))})"
        } ?: "Day ${position + 1}"
    }

    // Get destinations untuk hari tertentu
    fun getDestinationsForDay(dayIndex: Int): List<DestinationDummy> {
        return _selectedTrip.value?.destinations?.filter { destination ->
            // Logic untuk filter destinasi berdasarkan hari
            // Ini hanya contoh sederhana, sesuaikan dengan kebutuhan
            true
        } ?: emptyList()
    }

    // Ambil tanggal awal dan akhir perjalanan
    fun getStartDate(): String? {
        return _selectedTrip.value?.startDate
    }

    fun getEndDate(): String? {
        return _selectedTrip.value?.endDate
    }

    private fun fetchTrips() {
        viewModelScope.launch {
            val trips = planRepository.getTrips()
            _allTrips.value = trips
        }
    }
}

