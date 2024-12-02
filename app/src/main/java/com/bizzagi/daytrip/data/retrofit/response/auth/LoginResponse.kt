package com.bizzagi.daytrip.data.retrofit.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,          // Status login berhasil atau tidak

    @SerializedName("message")
    val message: String,          // Pesan terkait status login

    @SerializedName("data")
    val data: UserData?,          // Data pengguna yang berhasil login

    @SerializedName("error")
    val error: Boolean?,          // Menyimpan status error (jika ada)

    @SerializedName("loginResult")
    val loginResult: Any?         // Menyimpan informasi lebih lanjut terkait login, bisa berupa objek
)

data class UserData(
    @SerializedName("uid")
    val uid: String,              // ID pengguna

    @SerializedName("email")
    val email: String,            // Email pengguna

    @SerializedName("name")
    val name: String,             // Nama pengguna

    @SerializedName("token")
    val token: String             // Token untuk autentikasi
)