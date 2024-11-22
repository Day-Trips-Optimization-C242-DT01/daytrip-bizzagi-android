package com.bizzagi.daytrip.data.retrofit.response

data class PlansDummy(
    val id: String,
    val cohort: String,
    val startDate: String,
    val endDate: String,
    val destinations: List<DestinationDummy>
)
