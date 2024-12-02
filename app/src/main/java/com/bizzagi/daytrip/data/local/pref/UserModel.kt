package com.bizzagi.daytrip.data.local.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val uid: String,
    val email: String,
    val name: String,
    val token: String,
    val isLoading: Boolean

) : Parcelable