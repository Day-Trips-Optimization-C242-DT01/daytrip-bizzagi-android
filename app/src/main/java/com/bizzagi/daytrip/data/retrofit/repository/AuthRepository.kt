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

    // Register User
    fun registerUser(name: String, email: String, password: String, repassword: String): LiveData<Result<RegisterResponse>> = liveData {
        try {
            emit(Result.Loading)  // Emit status loading sebelum request
            val res = apiService.register(RegisterRequest(name, email, password, repassword))

            // Cek apakah registrasi berhasil atau tidak berdasarkan respons
            if (res.status == 200 && res.body.success) {
                emit(Result.Success(res))  // Emit hasil jika berhasil
            } else {
                emit(Result.Error(res.body.message ?: "Unknown Error"))  // Emit error jika gagal
            }
        } catch (e: HttpException) {
            // Menangani error dari API menggunakan Gson
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, RegisterResponse::class.java)
                emit(Result.Error(parseError.body.message))  // Emit pesan error
            } catch (exception: Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
        }
    }

    // Login User
    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        try {
            emit(Result.Loading)  // Emit status loading sebelum request
            val res = apiService.login(LoginRequest(email, password))

            // Cek apakah login berhasil atau tidak berdasarkan respons
            if (res.error != null && !res.error) {
                emit(Result.Success(res))  // Emit hasil jika berhasil
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))  // Emit error jika gagal
            }

        } catch (e: HttpException) {
            // Menangani error dari API menggunakan Gson
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, LoginResponse::class.java)
                emit(Result.Error(parseError.message))  // Emit pesan error
            } catch (exception: Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
        }
    }

    // Menyimpan sesi pengguna
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)  // Simpan data sesi ke user preference
    }

    // Mendapatkan sesi pengguna
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()  // Ambil sesi dari user preference
    }

    // Logout user
    suspend fun logout() {
        userPreference.logout()  // Hapus data sesi saat logout
    }

    // Singleton pattern untuk memastikan hanya ada satu instance dari AuthRepository
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