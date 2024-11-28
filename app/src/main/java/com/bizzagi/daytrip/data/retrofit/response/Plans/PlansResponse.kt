package com.bizzagi.daytrip.data.retrofit.response.Plans

import com.google.gson.annotations.SerializedName

data class PlansResponse(

	@field:SerializedName("data")
	val data: List<Plan>,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)