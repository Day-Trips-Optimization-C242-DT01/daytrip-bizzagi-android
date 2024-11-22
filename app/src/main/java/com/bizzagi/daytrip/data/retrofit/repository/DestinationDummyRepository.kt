package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.retrofit.response.DestinationDummy

object DestinationDummyRepository {
    fun getDestinationDetails(destinationId: String) : DestinationDummy {
        return when (destinationId) {
            "dest1" -> DestinationDummy(
                id = "dest1",
                name = "Pantai Pandawa",
                latitude = -8.8133,
                longitude = 115.1761,
                address = "Jl. Pantai Pandawa, Kutuh, Kec. Uluwatu, Kabupaten Badung, Bali 80361",
                type = "Beach",
                imageUrl = "https://example.com/pantai-pandawa.jpg",
                openingHours = "07:00 - 18:00",
                closeHours = "18.00-07.00"
            )
            "dest2" -> DestinationDummy(
                id = "dest2",
                name = "Ubud Monkey Forest",
                latitude = -8.5015,
                longitude = 115.2588,
                address = "Jl. Monkey Forest, Ubud, Kecamatan Ubud, Kabupaten Gianyar, Bali 80571",
                type = "Nature",
                imageUrl = "https://example.com/ubud-monkey-forest.jpg",
                openingHours = "08:30 - 17:30",
                closeHours = "18.00-08.00"
            )
            else -> throw IllegalArgumentException("Invalid destination ID")
        }
    }
}