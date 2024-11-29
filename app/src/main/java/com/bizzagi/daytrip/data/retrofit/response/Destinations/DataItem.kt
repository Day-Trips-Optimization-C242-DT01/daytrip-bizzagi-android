package com.bizzagi.daytrip.data.retrofit.response.Destinations

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("types")
	val types: List<String>,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("primaryType")
	val primaryType: String,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("photosList")
	val photosList: List<String>,

	@field:SerializedName("opens")
	val opens: List<String>,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("closes")
	val closes: List<String>,

	@field:SerializedName("longitude")
	val longitude: Double,

	@field:SerializedName("rating")
	val rating: Any
)