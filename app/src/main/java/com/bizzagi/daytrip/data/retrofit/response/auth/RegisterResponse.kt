package com.bizzagi.daytrip.data.retrofit.response.auth

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("body")
    val body: RegisterBody
)

data class RegisterBody(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: RegisterData,
)

data class RegisterData(
    @SerializedName("uid")
    val uid: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("token")
    val token: String
)

data class RegisterErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: RegisterErrorData
)

data class RegisterErrorData(
    @SerializedName("error")
    val error: String,

    @SerializedName("path")
    val path: String
)