package com.bizzagi.daytrip.data.retrofit.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bizzagi.daytrip.data.retrofit.response.DestinationsData
import com.bizzagi.daytrip.data.retrofit.response.DestinationsResponse

class DestinationRepository {
    private val destinations = mapOf(
        "destination1" to DestinationsData(
            latitude = -1.234,
            longitude = 5.234,
            name = "Waterbom Bali",
            types = listOf("water_park", "tourist_attraction"),
            primaryType = "water_park",
            address = "Jalan abc, Bali",
            opens = listOf("09:00", "09:00", "09:00", "09:00", "09:00", "09:00", "09:00"),
            closes = listOf("17:00", "17:00", "17:00", "17:00", "17:00", "17:00", "17:00"),
            photosList = listOf("https://linkToStorageObject1", "https://linkToStorageObject2")
        ),
        "destination2" to DestinationsData(
            latitude = -1.123,
            longitude = 6.543,
            name = "Tanah Lot",
            types = listOf("temple", "tourist_attraction"),
            primaryType = "temple",
            address = "Jalan xyz, Bali",
            opens = listOf("08:00", "08:00", "08:00", "08:00", "08:00", "08:00", "08:00"),
            closes = listOf("18:00", "18:00", "18:00", "18:00", "18:00", "18:00", "18:00"),
            photosList = listOf("https://linkToStorageObject3", "https://linkToStorageObject4")
        ),
        "destination3" to DestinationsData (
            latitude = -8.7285829,
            longitude = 115.16929,
            name = "Waterbom Bali",
            types = listOf("water_park", "tourist_attraction","amusement_park"),
            primaryType = "water_park",
            address = "Jl. Kartika Plaza, Tuban, Kec. Kuta, Kabupaten Badung, Bali 80361, Indonesia",
            opens = listOf("08:00", "08:00", "08:00", "08:00", "08:00", "08:00", "08:00"),
            closes = listOf("18:00", "18:00", "18:00", "18:00", "18:00", "18:00", "18:00"),
            photosList = listOf("https://linkToStorageObject3", "https://linkToStorageObject4")
        ),
        "destination4" to DestinationsData (
            latitude = -8.5071639,
            longitude = 115.26297249999999,
            name = "Ubud Art Market",
            types = listOf("market", "point_of_interest","establishment"),
            primaryType = "market",
            address = "Jl. Raya Ubud No.35, Ubud, Kecamatan Ubud, Kabupaten Gianyar, Bali 80571, Indonesia",
            opens = listOf("08:00", "08:00", "08:00", "08:00", "08:00", "08:00", "08:00"),
            closes = listOf("18:00", "18:00", "18:00", "18:00", "18:00", "18:00", "18:00"),
            photosList = listOf("https://linkToStorageObject3", "https://linkToStorageObject4")
        )
    )
    fun getDestinations(destinationId:String): DestinationsResponse {
        val destinationData = destinations[destinationId]
        return if (destinationData != null) {
            DestinationsResponse(
                success = true,
                message = "Destination retrieved successfully",
                data = destinationData
            )
        } else {
            DestinationsResponse(
                success = false,
                message = "Destination not found",
                data = DestinationsData(
                    latitude = 0.0,
                    longitude = 0.0,
                    name = "Unknown",
                    types = emptyList(),
                    primaryType = "unknown",
                    address = "unknown",
                    opens = emptyList(),
                    closes = emptyList(),
                    photosList = emptyList()
                )
            )
        }
    }
}