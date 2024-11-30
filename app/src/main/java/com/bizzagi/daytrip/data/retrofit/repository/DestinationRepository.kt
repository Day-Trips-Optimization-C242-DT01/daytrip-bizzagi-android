package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationPostResponse
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationsResponse
import com.google.gson.Gson
import retrofit2.HttpException

class DestinationRepository (private val apiService: ApiService) {
    suspend fun createDestination(planId: String) : Result<DestinationPostResponse> {
        return try {
            val response = apiService.creteDestination(planId)
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

    suspend fun getDestinations(destinationIds: List<String>): Result<DestinationsResponse> {
        return try {
            val response = apiService.getDestinations()
            if (!response.success) {
                Result.Error(response.message)
            } else {
                val filteredResponse = response.copy(
                    data = response.data.filter { it.id in destinationIds }
                )
                Result.Success(filteredResponse)
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
        private var instance: DestinationRepository? = null
        fun getInstance(
            apiService: ApiService
        ): DestinationRepository =
            instance ?: synchronized(this) {
                instance?: DestinationRepository(apiService)
            }. also { instance = it }
    }
}