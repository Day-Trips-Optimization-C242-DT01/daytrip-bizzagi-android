package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.local.pref.UserModel
import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.model.LoginRequest
import com.bizzagi.daytrip.data.retrofit.model.RegisterRequest
import com.bizzagi.daytrip.data.retrofit.response.auth.LoginResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.RegisterResponse
import com.bizzagi.daytrip.utils.Result
import com.bizzagi.daytrip.data.local.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson

class AuthRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun registerUser(name: String, email: String, password: String, repassword: String): LiveData<Result<RegisterResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.register(RegisterRequest(name, email, password, repassword))

            if (res.status == 200 && res.body.success) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.body.message ?: "Unknown Error"))
            }
        } catch (e: HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, RegisterResponse::class.java)
                emit(Result.Error(parseError.body.message))
            } catch (exception: Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
        }
    }

    // Login User
    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.login(LoginRequest(email, password))

            if (res.error != null && !res.error) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }

        } catch (e: HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, LoginResponse::class.java)
                emit(Result.Error(parseError.message))
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