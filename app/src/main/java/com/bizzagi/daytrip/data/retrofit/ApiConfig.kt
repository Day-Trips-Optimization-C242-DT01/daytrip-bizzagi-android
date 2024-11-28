package com.bizzagi.daytrip.data.retrofit

import com.bizzagi.daytrip.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        val BASE_URL = BuildConfig.BASE_URL

        fun getApiService(): ApiService {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor {chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization","Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkyODg2OGRjNDRlYTZhOThjODhiMzkzZDM2NDQ1MTM2NWViYjMwZDgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vYzI0Mi1kdDAxIiwiYXVkIjoiYzI0Mi1kdDAxIiwiYXV0aF90aW1lIjoxNzMyNzU2NjkxLCJ1c2VyX2lkIjoiVEJ0UkEySTF0UmdBV3FERlpGQXcwT3premhEMyIsInN1YiI6IlRCdFJBMkkxdFJnQVdxREZaRkF3ME96a3poRDMiLCJpYXQiOjE3MzI3NTY2OTEsImV4cCI6MTczMjc2MDI5MSwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInRlc3RAdGVzdC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.Hjx5mPYjJXgT_LRj3J_UT0DBHTsOo_rmR0Z9hKFq9k5lYpea33Hzfah2ZUsraiecQcxnsvDu1jrhlYHArweUqgzcYCuN_BRvmoOKCGDJddyuGbyRERHrFUOo2yfrlYN6_jh8gZjKswR36sD75MFOcTUCMrTFLCipWgV4inVhS5u32BPFdDvUoa-aD2b7mcJ3A6X6DGvGYoJ-b9OQb9xeqipCcHP_t_KV981_zIYKxx2oUJnz9vzbdfRYRa2Z33VT10GY4W2pV8WO13zkGUs5rvkqgbEtzdmP1OWzKQoWmOrb-A6znpDZsXeVNFlDzSo6Isebth0oe6XQpgxIJm4X8g")
                        .build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}