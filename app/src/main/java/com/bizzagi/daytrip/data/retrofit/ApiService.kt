package com.bizzagi.daytrip.data.retrofit

import com.bizzagi.daytrip.data.retrofit.model.LoginRequest
import com.bizzagi.daytrip.data.retrofit.model.RegisterRequest
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationPostResponse
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DestinationsResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.CreatePlanRequest
import com.bizzagi.daytrip.data.retrofit.response.Plans.DeletePlanResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlanPostResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlansResponse
import com.bizzagi.daytrip.data.retrofit.response.Plans.UpdatePlanRequest
import com.bizzagi.daytrip.data.retrofit.response.Plans.UpdatePlanResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.LoginResponse
import com.bizzagi.daytrip.data.retrofit.response.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(
        @Body requestBody: LoginRequest
    ): Response<LoginResponse>

    @POST("destinations/create/{planId}")
    suspend fun creteDestination (
        @Path("planId") planId:String
    ): DestinationPostResponse

    @GET("destinations/list")
    suspend fun getDestinations() : DestinationsResponse

    @POST("plans/create")
    suspend fun createPlan (
       @Body createPlanRequest: CreatePlanRequest
    ):PlanPostResponse

    @GET("plans/list")
    suspend fun getPlans() : PlansResponse

    @PUT("plans/update/{planId}")
    suspend fun updatePlan(
        @Path("planId") planId: String,
        @Body updatePlanRequest: UpdatePlanRequest
    ): UpdatePlanResponse

    @DELETE("plans/delete/{planId}")
    suspend fun deletePlan (
        @Path("planId") planId:String
    ): DeletePlanResponse

}