package com.bizzagi.daytrip.data.retrofit.response.Plans

data class UpdatePlanRequest (
    val days: Map<String, List<String>>
)