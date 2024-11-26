package com.bizzagi.daytrip.data.retrofit.response

data class DestinationsResponse (
    val success: Boolean,
    val message: String,
    val data: DestinationsData
)