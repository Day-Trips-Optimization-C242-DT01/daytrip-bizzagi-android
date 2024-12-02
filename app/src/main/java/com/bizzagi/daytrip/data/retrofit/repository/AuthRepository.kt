package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.local.pref.UserModel
import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.model.LoginRequest
import com.bizzagi.daytrip.data.retrofit.model.RegisterRequest
import com.bizzagi.daytrip.data.retrofit.response.auth.LoginResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.RegisterResponse
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.local.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun registerUser(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val requestBody = RegisterRequest(
                name = name,
                email = email,
                password = password
            )

            val response = apiService.register(requestBody)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception occurred: ${e.message}")
        }
    }

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email, password)

            val response = apiService.login(loginRequest)

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!

                if (responseBody.success) {
                    Result.Success(responseBody)
                } else {
                    Result.Error(responseBody.message ?: "Unknown error")
                }
            } else {
                Result.Error("Error: ${response.message() ?: "Unknown error"}")
            }
        } catch (e: Exception) {
            Result.Error("Exception occurred: ${e.message}")
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService, userPreference).also { INSTANCE = it }
            }
    }
}