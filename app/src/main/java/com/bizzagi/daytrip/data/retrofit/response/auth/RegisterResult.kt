package com.bizzagi.daytrip.data.retrofit.response.auth

data class RegisterResult(
    val uid: String?,
    val email: String?,
    val name: String?,
    val token: String?
)