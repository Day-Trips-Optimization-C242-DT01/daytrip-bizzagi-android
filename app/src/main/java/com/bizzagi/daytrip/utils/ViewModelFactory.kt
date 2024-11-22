package com.bizzagi.daytrip.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizzagi.daytrip.data.retrofit.repository.PlansDummyRepository
import com.bizzagi.daytrip.ui.Trip.PlansViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (private val planRepository: PlansDummyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansViewModel::class.java)) {
            return PlansViewModel(planRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}