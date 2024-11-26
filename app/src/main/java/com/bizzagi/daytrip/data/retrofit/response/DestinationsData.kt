package com.bizzagi.daytrip.data.retrofit.response

data class DestinationsData(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val types: List<String>,
    val primaryType: String,
    val address: String,
    val opens: List<String>,
    val closes: List<String>,
    val photosList: List<String>
)
