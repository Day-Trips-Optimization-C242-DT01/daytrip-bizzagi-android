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
import retrofit2.HttpException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import retrofit2.Response

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

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.login(LoginRequest(email, password))

            if (res.isSuccessful && res.body() != null) {
                val responseBody = res.body()!!

                if (responseBody.error == false) {
                    emit(Result.Success(responseBody))
                } else {
                    emit(Result.Error(responseBody.message ?: "Unknown Error"))
                }
            } else {
                emit(Result.Error("Error: ${res.message()}"))
            }

        } catch (e: HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, LoginResponse::class.java)
                emit(Result.Error(parseError.message ?: "Unknown Error"))
            } catch (exception: Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
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