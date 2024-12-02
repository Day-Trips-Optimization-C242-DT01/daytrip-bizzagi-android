package com.bizzagi.daytrip.data.retrofit.response.Plans

import com.google.gson.annotations.SerializedName

data class PlanPostResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("start_date")
	val startDate: String,

	@field:SerializedName("end_date")
	val endDate: String,

	@SerializedName("data")
	val data: Map<String, List<String>>,
)
