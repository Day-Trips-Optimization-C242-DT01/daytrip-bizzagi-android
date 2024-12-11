package com.bizzagi.daytrip.data.retrofit.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData,

    @SerializedName("error")
    val error: Boolean?,
)

data class UserData(
    @SerializedName("uid")
    val uid: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("token")
    val token: String
)

data class LoginErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LoginErrorData
)

data class LoginErrorData(
    @SerializedName("error")
    val error: String,

    @SerializedName("path")
    val path: String
)