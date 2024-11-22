package com.bizzagi.daytrip.data.retrofit.response

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
)
