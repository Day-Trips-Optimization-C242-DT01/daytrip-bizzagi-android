package com.bizzagi.daytrip.data.retrofit.response.Plans

import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("id")
    val id: String,

    @SerializedName("data")
    val data: Map<String, List<String>>,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("plan_name")
    val planName : String,
)