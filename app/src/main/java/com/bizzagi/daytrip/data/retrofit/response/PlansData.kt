package com.bizzagi.daytrip.data.retrofit.response

import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem

data class PlansData(
    val days: Map<String, List<DataItem>> // Key: "day1", "day2", ..., Value: List of destination IDs
)