package com.bizzagi.daytrip.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizzagi.daytrip.data.retrofit.repository.AuthRepository
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.injection.Injection
import com.bizzagi.daytrip.ui.Maps.MapsViewModel
import com.bizzagi.daytrip.ui.Trip.PlansViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (
    private val plansRepository: PlansRepository,
    private val destinationsRepository: DestinationRepository,
    private val authRepository: AuthRepository,

    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansViewModel::class.java)) {
            return PlansViewModel(plansRepository,destinationsRepository) as T
        }
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(destinationsRepository) as T
        }
        if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            return AuthenticationViewModel(authRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE : ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory{
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.providePlansRepository(),
                        Injection.provideDestinationsRepository(),
                        Injection.provideAuthRepository(context)
                        )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}