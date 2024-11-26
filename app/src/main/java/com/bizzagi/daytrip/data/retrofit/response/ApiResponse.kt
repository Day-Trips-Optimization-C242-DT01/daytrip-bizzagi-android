package com.bizzagi.daytrip.data.retrofit.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)