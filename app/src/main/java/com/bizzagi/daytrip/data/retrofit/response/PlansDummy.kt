package com.bizzagi.daytrip.data.retrofit.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlansDummy(
    val id: String,
    val cohort: String,
    val startDate: String,
    val endDate: String,
    val destinations: List<DestinationDummy>
) : Parcelable
