package com.bizzagi.daytrip.data.retrofit

import com.bizzagi.daytrip.data.retrofit.model.LoginRequest
import com.bizzagi.daytrip.data.retrofit.model.RegisterRequest
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationPostResponse
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationsResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlansResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.LoginResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/signup")
    suspend fun register(@Body requestBody: RegisterRequest): RegisterResponse
    @POST("auth/login")
    suspend fun login(@Body requestBody: LoginRequest): LoginResponse
    @POST("destinations/create/{planId}")
    suspend fun creteDestination (
        @Path("planId") planId:String
    ): DestinationPostResponse
    @GET("destinations/list")
    suspend fun getDestinations() : DestinationsResponse

    @GET("plans/list")
    suspend fun getPlans() : PlansResponse
}