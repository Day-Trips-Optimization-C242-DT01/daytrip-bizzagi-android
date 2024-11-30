package com.bizzagi.daytrip.ui.Maps

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationPostResponse
import kotlinx.coroutines.launch

class MapsViewModel (
    private val destinationRepository: DestinationRepository
) : ViewModel() {
    private val _destinationResult = MutableLiveData<Result<DestinationPostResponse>>()
    val destinationResult: MutableLiveData<Result<DestinationPostResponse>> get() = _destinationResult
    fun postDestination (planId: String) {
        viewModelScope.launch {
            _destinationResult.value = Result.Loading
            kotlinx.coroutines.delay(1000)
            val result = destinationRepository.createDestination(planId)
            Log.d("MapViewModel", "destinations received: $result")
            _destinationResult.value = result
        }
    }
}