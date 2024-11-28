package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationsResponse
import com.google.gson.Gson
import retrofit2.HttpException

class DestinationRepository (private val apiService: ApiService) {
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
}