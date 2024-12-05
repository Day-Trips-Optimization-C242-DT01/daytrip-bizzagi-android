package com.bizzagi.daytrip.ui.Maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationPostResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.CreatePlanRequest
import com.bizzagi.daytrip.data.retrofit.response.Plans.LokasiUser
import com.bizzagi.daytrip.data.retrofit.response.Plans.Place
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlanPostResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapsViewModel (
    private val plansRepository: PlansRepository,
    private val destinationRepository: DestinationRepository
) : ViewModel() {
    private val _planResult = MutableLiveData<Result<PlanPostResponse>>()
    val planResult: MutableLiveData<Result<PlanPostResponse>> get() = _planResult

    private val _destinationResult = MutableLiveData<Result<DestinationPostResponse>>()
    val destinationResult: MutableLiveData<Result<DestinationPostResponse>> get() = _destinationResult

    private val _places = MutableLiveData<List<Place>>(emptyList())
    val places: LiveData<List<Place>> get() = _places

    private val _selectedLocation = MutableLiveData<LatLng?>()
    val selectedLocation: LiveData<LatLng?> get() = _selectedLocation

    private val _destinationsPerDay = MutableLiveData<Map<String, List<DataItem>>>()
    val destinationsPerDay: LiveData<Map<String, List<DataItem>>> get() = _destinationsPerDay

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchDestinationsPerDay() {
        viewModelScope.launch {
            try {
                val destinationsByDay = plansRepository.getDestinationsOfDay()

                val allDestinationIds = destinationsByDay.values.flatten()

                val result = destinationRepository.getDestinations(allDestinationIds)
                if (result is Result.Success) {
                    val destinationData = result.data.data

                    val mappedDestinations = destinationsByDay.mapValues { (_, destinationIds) ->
                        destinationData.filter { it.id in destinationIds }
                    }

                    _destinationsPerDay.postValue(mappedDestinations)
                } else if (result is Result.Error) {
                    Log.e("MapViewModel", "Error fetching destination details: ${result.message}")
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Unexpected error: ${e.message}")
            }
        }
    }

    fun setSelectedLocation(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        _selectedLocation.value = latLng
        Log.d("MapsViewModel", "setSelectedLocation: Data masuk -> $latLng")
    }


    fun clearSelectedLocation() {
        _selectedLocation.value = null
    }

    fun addPlace(place: Place) {
        val updatedPlaces = _places.value?.toMutableList() ?: mutableListOf()
        updatedPlaces.add(place)
        _places.value = updatedPlaces
    }

    fun clearPlaces() {
        // Kosongkan daftar
        _places.value = emptyList()
    }

    fun postPlans(
        uid: String,
        numDays: Int,
        planName: String,
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String
    ) {
        val request = CreatePlanRequest(
           uid = uid,
           num_days = numDays,
           plan_name = planName,
           lokasi_user = LokasiUser(
               latitude = latitude,
               longitude = longitude
           ),
           places = _places.value ?: emptyList(),
           start_date = startDate,
           end_date = endDate
        )
        viewModelScope.launch {

            val result = plansRepository.createPlan(request)
            _planResult.value = result
        }
    }

    fun postDestination (planId: String) {
        viewModelScope.launch {
            _destinationResult.value = Result.Loading(true)
            kotlinx.coroutines.delay(1000)
            val result = destinationRepository.createDestination(planId)
            Log.d("MapViewModel", "destinations received: $result")
            _destinationResult.value = result
        }
    }
}