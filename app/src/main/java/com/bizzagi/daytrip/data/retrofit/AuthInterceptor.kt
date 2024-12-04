package com.bizzagi.daytrip.data.retrofit

import android.util.Log
import com.bizzagi.daytrip.data.local.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreference: UserPreference): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val userData = runBlocking {
            userPreference.getSession().first()
        }

        val token = userData?.token ?: ""
        Log.d("AuthInterceptor", "Using token: $token")

        if (token.isBlank()) {
            Log.e("AuthInterceptor", "Token is blank or empty")
        }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}