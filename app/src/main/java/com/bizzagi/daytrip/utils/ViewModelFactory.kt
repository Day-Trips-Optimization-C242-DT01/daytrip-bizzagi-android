package com.bizzagi.daytrip.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.ui.Trip.PlansViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (
    private val plansRepository: PlansRepository,
    private val destinationsRepository: DestinationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansViewModel::class.java)) {
            return PlansViewModel(plansRepository,destinationsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}