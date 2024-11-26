package com.bizzagi.daytrip.data.retrofit.response

data class PlansResponse(
    val success: Boolean,
    val message: String,
    val data: PlansData
)