package com.bizzagi.daytrip.injection

import android.content.Context
import com.bizzagi.daytrip.data.local.pref.UserPreference
import com.bizzagi.daytrip.data.local.pref.dataStore
import com.bizzagi.daytrip.data.retrofit.ApiConfig
import com.bizzagi.daytrip.data.retrofit.repository.AuthRepository
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository

object Injection {
    fun provideDestinationsRepository () : DestinationRepository {
        val apiService = ApiConfig.getApiService()
        return DestinationRepository.getInstance(apiService)
    }
    fun providePlansRepository() : PlansRepository {
        val apiService = ApiConfig.getApiService()
        return PlansRepository.getInstance(apiService)
    }
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, pref)
    }
}