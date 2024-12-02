package com.bizzagi.daytrip.data.retrofit.response.auth

data class LoginResult(
    val token: String?,
    val userId: String?,
    val username: String?,
)