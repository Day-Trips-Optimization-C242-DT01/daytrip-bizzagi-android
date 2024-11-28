package com.bizzagi.daytrip.data.retrofit.repository

import android.util.Log
import com.bizzagi.daytrip.data.retrofit.ApiService

class PlansRepository(
    private val apiService: ApiService
) {
    suspend fun getPlanIds(): List<String> {
        return try {
            val response = apiService.getPlans()
            if (!response.success) {
                Log.e("getPlanIds", "API response unsuccessful: ${response.message}")
                emptyList()
            } else {
                val planIds = response.data.map { it.id }
                Log.d("getPlanIds", "Fetched Plan IDs: $planIds")
                planIds
            }
        } catch (e: Exception) {
            Log.e("getPlanIds", "Error fetching Plan IDs: ${e.message}")
            emptyList()
        }
    }

    suspend fun getDays(planId: String): Map<String, List<String>> {
        return try {
            val response = apiService.getPlans()
            if (!response.success) {
                Log.e("getDays", "API response unsuccessful: ${response.message}")
                emptyMap()
            } else {
                val plan = response.data.find { it.id == planId }
                if (plan != null) {
                    Log.d("getDays", "Fetched days for Plan ID $planId: ${plan.data}")
                    plan.data
                } else {
                    Log.e("getDays", "Plan not found for ID: $planId")
                    emptyMap()
                }
            }
        } catch (e: Exception) {
            Log.e("getDays", "Error fetching days for Plan ID $planId: ${e.message}")
            emptyMap()
        }
    }
}
