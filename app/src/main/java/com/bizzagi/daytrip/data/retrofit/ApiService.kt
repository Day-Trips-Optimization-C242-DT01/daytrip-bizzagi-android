package com.bizzagi.daytrip.data.retrofit

import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationPostResponse
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationsResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlansResponse
import retrofit2.http.*

interface ApiService {
    @POST("destinations/create/{planId}")
    suspend fun creteDestination (
        @Path("planId") planId:String
    ): DestinationPostResponse
    @GET("destinations/list")
    suspend fun getDestinations() : DestinationsResponse

    @GET("plans/list")
    suspend fun getPlans() : PlansResponse
}