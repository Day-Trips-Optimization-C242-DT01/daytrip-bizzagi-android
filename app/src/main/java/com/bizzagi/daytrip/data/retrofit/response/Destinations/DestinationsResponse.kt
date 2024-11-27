package com.bizzagi.daytrip.data.retrofit.response.Destinations

import com.google.gson.annotations.SerializedName

data class DestinationsResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)