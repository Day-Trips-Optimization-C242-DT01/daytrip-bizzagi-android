package com.bizzagi.daytrip.data.retrofit.response.Plans

import com.google.gson.annotations.SerializedName

data class DeletePlanResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
