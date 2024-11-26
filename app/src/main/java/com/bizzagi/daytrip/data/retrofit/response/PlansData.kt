package com.bizzagi.daytrip.data.retrofit.response

data class PlansData(
    val days: Map<String, List<DestinationsData>> // Key: "day1", "day2", ..., Value: List of destination IDs
)