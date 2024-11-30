package com.bizzagi.daytrip.data.retrofit.response.Destinations

import com.google.gson.annotations.SerializedName

data class DestinationPostResponse(

	@field:SerializedName("data")
	val data: DataItem,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)