package com.bizzagi.daytrip.data.retrofit.response.auth

data class LoginResult(
    val token: String?,
    val uid: String?,
    val username: String?,
    val email: String?,
)