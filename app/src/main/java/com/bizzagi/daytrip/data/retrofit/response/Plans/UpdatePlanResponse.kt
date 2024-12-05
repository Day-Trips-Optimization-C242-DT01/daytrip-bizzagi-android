package com.bizzagi.daytrip.data.retrofit.response.Plans

import com.google.gson.annotations.SerializedName

data class UpdatePlanResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DayData(
	val days: Map<String, List<String>>
)
