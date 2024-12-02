package com.bizzagi.daytrip.data.retrofit.model

data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
    val repassword: String
)