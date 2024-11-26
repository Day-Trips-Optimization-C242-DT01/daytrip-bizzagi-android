package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.retrofit.response.DestinationsData
import com.bizzagi.daytrip.data.retrofit.response.PlansData
import com.bizzagi.daytrip.data.retrofit.response.PlansResponse

class PlansRepository (private val destinationRepository: DestinationRepository){
    private val plans = mapOf(
        "plan1" to mapOf(
            "day1" to listOf("destination1", "destination2", "destination3"),
            "day2" to listOf("destination4", "destination5", "destination1"),
            "day3" to listOf("destination3", "destination2", "destination4")
        ),
        "plan2" to mapOf(
            "day1" to listOf("destination1", "destination2"),
            "day2" to listOf("destination3", "destination4"),
            "day3" to listOf("destination5", "destination1"),
            "day4" to listOf("destination2"),
            "day5" to listOf("destination4"),
            "day6" to listOf("destination3")
        )
    )

    fun getPlanIds(): List<String> {
        return plans.keys.toList()
    }

    fun getDays(planId: String) : PlansResponse {
        val daysData = plans[planId]?.map { entry ->
            entry.key
        } ?: emptyList()

        return PlansResponse(
            success = true,
            message = if (daysData.isNotEmpty()) "Days retrieved successfully" else "No days found for this plan",
            data = PlansData(days = daysData.associateWith { emptyList() })
        )
    }

    fun getDestinationsByDay(planId: String, day: String): List<DestinationsData> {
        val destinationIds = plans[planId]?.get(day) ?: emptyList()
        return destinationIds.mapNotNull { id ->
            val response = destinationRepository.getDestinations(id)
            if (response.success) response.data else null
        }
    }
}