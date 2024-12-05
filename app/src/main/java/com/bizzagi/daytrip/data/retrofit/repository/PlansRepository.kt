package com.bizzagi.daytrip.data.retrofit.repository

import android.util.Log
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationsResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.CreatePlanRequest
import com.bizzagi.daytrip.data.retrofit.response.Plans.DeletePlanResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.Plan
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlanPostResponse
import com.google.gson.Gson
import retrofit2.HttpException

class PlansRepository(
    private val apiService: ApiService
) {
    suspend fun createPlan(request: CreatePlanRequest) : Result<PlanPostResponse> {
        return try {
            val response = apiService.createPlan(request)
            if (!response.success) {
                Result.Error(response.message)
            } else {
                Result.Success(response)
            }
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, DestinationsResponse::class.java)
                Result.Error(errorResponse.message)
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
    }
    suspend fun getPlansDestinations(): List<String> {
        return try {
            val response = apiService.getPlans()
            Log.d("getPlanDestinations", "API response: $response")
            if (!response.success) {
                Log.e("getPlanDestinations", "API response unsuccessful: ${response.message}")
                emptyList()
            } else {
                val destinationids = response.data.flatMap { plan ->
                    plan.data.values.flatten()
                }
                destinationids.distinct()
            }
        } catch (e: HttpException) {
            Log.e("getPlanDestinations", "Error fetching all destination IDs: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPlanIds(): List<Plan> {
        return try {
            val response = apiService.getPlans()
            if (!response.success) {
                Log.e("getPlanIds", "API response unsuccessful: ${response.message}")
                emptyList()
            } else {
                val plans = response.data.map { apiResponse ->
                    Plan(
                        id = apiResponse.id,
                        data = apiResponse.data,
                        startDate = apiResponse.startDate,
                        endDate = apiResponse.endDate,
                        planName = apiResponse.planName
                    )
                }
                Log.d("getPlanIds", "Fetched Plans: $plans")
                plans
            }
        } catch (e: Exception) {
            Log.e("getPlanIds", "Error fetching Plans: ${e.message}")
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

    suspend fun getDestinationsOfDay(): Map<String, List<String>> {
        return try {
            val response = apiService.getPlans()
            if (!response.success) {
                Log.e("getDestinationsOfDay", "API response unsuccessful: ${response.message}")
                emptyMap()
            } else {
                val allDestinations = mutableMapOf<String, List<String>>()
                response.data.forEach { plan ->
                    plan.data.forEach { (day, destinations) ->
                        allDestinations[day] = destinations
                    }
                }
                allDestinations
            }
        } catch (e: Exception) {
            Log.e("getDestinationsOfDay", "Error fetching destinations: ${e.message}")
            emptyMap()
        }
    }


    suspend fun deletePlan(planId: String) : Result<DeletePlanResponse> {
        return try {
            val response = apiService.deletePlan(planId)
            if (!response.success) {
                Result.Error(response.message)
            } else {
                Result.Success(response)
            }
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, DestinationsResponse::class.java)
                Result.Error(errorResponse.message)
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
    }

    companion object {
        @Volatile
        private var instance: PlansRepository? = null
        fun getInstance(
            apiService: ApiService
        ): PlansRepository =
            instance ?: synchronized(this) {
                instance?: PlansRepository(apiService)
            }. also { instance = it }
    }
}
