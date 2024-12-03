package com.bizzagi.daytrip.injection

import android.content.Context
import com.bizzagi.daytrip.data.local.pref.UserPreference
import com.bizzagi.daytrip.data.local.pref.dataStore
import com.bizzagi.daytrip.data.retrofit.ApiConfig
import com.bizzagi.daytrip.data.retrofit.repository.AuthRepository
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository

object Injection {
    fun provideDestinationsRepository (context: Context) : DestinationRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return DestinationRepository.getInstance(apiService)
    }
    fun providePlansRepository(context: Context) : PlansRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return PlansRepository.getInstance(apiService)
    }
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, pref)
    }
}