package com.bizzagi.daytrip.data.retrofit.response
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class DestinationDummy(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val imageUrl: String?,
    val type: String,
    val openingHours: String?,
    val closeHours: String?
) : Parcelable
