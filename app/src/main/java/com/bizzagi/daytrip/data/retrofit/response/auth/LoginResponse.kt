
package com.bizzagi.daytrip.data.retrofit.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData?,

    @SerializedName("error")
    val error: Boolean?,

    @SerializedName("loginResult")
    val loginResult: Any?
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
