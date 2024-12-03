package com.bizzagi.daytrip.data.retrofit.response.Plans

data class CreatePlanRequest (
    val uid: String,
    val num_days: Int,
    val plan_name: String,
    val lokasi_user: LokasiUser,
    val places: List<Place>,
    val start_date: String,
    val end_date: String
)

data class LokasiUser(
    val latitude: Double,
    val longitude: Double
)

data class Place(
    val place_id: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double?,
    val open_time: String,
    val close_time: String
)