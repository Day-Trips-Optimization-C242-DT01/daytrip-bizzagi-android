package com.bizzagi.daytrip.data.retrofit.repository

import com.bizzagi.daytrip.data.retrofit.response.DestinationDummy
import com.bizzagi.daytrip.data.retrofit.response.PlansDummy

object PlansDummyRepository {
    fun getTrips(): List<PlansDummy> {
        return listOf(
            PlansDummy(
                id = "plans1",
                cohort = "November",
                startDate = "2024-11-11",
                endDate = "2024-11-16",
                destinations = listOf(
                    DestinationDummy(
                        id = "dest1",
                        name = "Pantai Pandawa",
                        latitude = -8.8133,
                        longitude = 115.1761,
                        address = "Jl. Pantai Pandawa, Kutuh, Kec. Uluwatu, Kabupaten Badung, Bali 80361",
                        type = "Beach",
                        imageUrl = "https://example.com/pantai-pandawa.jpg",
                        openingHours = "07:00 - 18:00",
                        closeHours = "18.00-07.00"
                    ),
                    DestinationDummy(
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
                )
            ),
            PlansDummy(
                id = "plans2",
                cohort = "Januari",
                startDate = "2024-11-11",
                endDate = "2024-11-16",
                destinations = listOf(
                    DestinationDummy(
                        id = "dest1",
                        name = "Pantai Pandawa",
                        latitude = -8.8133,
                        longitude = 115.1761,
                        address = "Jl. Pantai Pandawa, Kutuh, Kec. Uluwatu, Kabupaten Badung, Bali 80361",
                        type = "Beach",
                        imageUrl = "https://example.com/pantai-pandawa.jpg",
                        openingHours = "07:00 - 18:00",
                        closeHours = "18.00-07.00"
                    ),
                    DestinationDummy(
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
                )
            ),
            PlansDummy(
                id = "plans3",
                cohort = "Oktober",
                startDate = "2024-11-11",
                endDate = "2024-11-16",
                destinations = listOf(
                    DestinationDummy(
                        id = "dest1",
                        name = "Pantai Pandawa",
                        latitude = -8.8133,
                        longitude = 115.1761,
                        address = "Jl. Pantai Pandawa, Kutuh, Kec. Uluwatu, Kabupaten Badung, Bali 80361",
                        type = "Beach",
                        imageUrl = "https://example.com/pantai-pandawa.jpg",
                        openingHours = "07:00 - 18:00",
                        closeHours = "18.00-07.00"
                    ),
                    DestinationDummy(
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
                )
            )
        )
    }
}