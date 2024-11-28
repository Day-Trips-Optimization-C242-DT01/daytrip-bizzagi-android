package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.data.retrofit.response.PlansData
import com.bizzagi.daytrip.data.retrofit.response.PlansResponse
import com.bizzagi.daytrip.data.Result

class PlansRepository(private val destinationRepository: DestinationRepository) {
    private val plans = mapOf(
        "plan1" to mapOf(
            "day1" to listOf("ChIJ34KP7qNG0i0RpX9A85PWkRI", "ChIJWUManuA90i0RPiAmw5hBOIc", "ChIJYaJ20FDtaS4RDNry7LknMQc"),
            "day2" to listOf("ChIJl3dU9cFG0i0RE_JTvoaxSVQ", "ChIJ34KP7qNG0i0RpX9A85PWkRI", "ChIJYaJ20FDtaS4RDNry7LknMQc"),
            "day3" to listOf("ChIJWUManuA90i0RPiAmw5hBOIc", "ChIJYaJ20FDtaS4RDNry7LknMQc", "ChIJ34KP7qNG0i0RpX9A85PWkRI")
        ),
        "plan2" to mapOf(
            "day1" to listOf("ChIJ34KP7qNG0i0RpX9A85PWkRI", "ChIJWUManuA90i0RPiAmw5hBOIc"),
            "day2" to listOf("ChIJYaJ20FDtaS4RDNry7LknMQc", "ChIJ34KP7qNG0i0RpX9A85PWkRI"),
            "day3" to listOf("ChIJWUManuA90i0RPiAmw5hBOIc", "ChIJ34KP7qNG0i0RpX9A85PWkRI"),
            "day4" to listOf("ChIJ34KP7qNG0i0RpX9A85PWkRI"),
            "day5" to listOf("ChIJWUManuA90i0RPiAmw5hBOIc"),
            "day6" to listOf("ChIJYaJ20FDtaS4RDNry7LknMQc")
        )
    )

    fun getPlanIds(): List<String> {
        return plans.keys.toList()
    }

    fun getDays(planId: String): PlansResponse {
        val daysData = plans[planId]?.map { entry ->
            entry.key
        } ?: emptyList()

        return PlansResponse(
            success = true,
            message = if (daysData.isNotEmpty()) "Days retrieved successfully" else "No days found for this plan",
            data = PlansData(days = daysData.associateWith { emptyList() })
        )
    }

    suspend fun getDestinationsByDay(planId: String, day: String): List<DataItem> {
        val destinationIds = plans[planId]?.get(day) ?: return emptyList()

        return when (val response = destinationRepository.getDestinations(destinationIds)) {
            is Result.Success -> response.data.data
            is Result.Error -> emptyList()
            is Result.Loading -> emptyList()
        }
    }
}