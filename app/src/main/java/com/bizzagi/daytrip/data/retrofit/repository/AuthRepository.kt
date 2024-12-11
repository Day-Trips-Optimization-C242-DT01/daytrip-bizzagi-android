package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.model.LoginRequest
import com.bizzagi.daytrip.data.retrofit.model.RegisterRequest
import com.bizzagi.daytrip.data.retrofit.response.auth.LoginResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.RegisterResponse
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.local.pref.UserPreference
import com.bizzagi.daytrip.data.retrofit.response.auth.LoginErrorResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.RegisterErrorResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.UserData
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

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
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterErrorResponse::class.java)
                Result.Error(errorResponse.data.error)
            }
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterErrorResponse::class.java)
                Result.Error(errorResponse.data.error)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Connection error occurred")
        }
    }

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email, password)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)
                Result.Error(errorResponse.data.error)
            }
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)
                Result.Error(errorResponse.data.error)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Authentication failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Connection error occurred")
        }
    }

    suspend fun saveSession(user: UserData) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserData?> {
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