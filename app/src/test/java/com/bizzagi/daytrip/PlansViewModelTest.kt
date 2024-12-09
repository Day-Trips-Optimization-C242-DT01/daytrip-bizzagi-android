package com.bizzagi.daytrip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bizzagi.daytrip.data.retrofit.ApiService
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.Plans.CreatePlanRequest
import com.bizzagi.daytrip.data.retrofit.response.Plans.LokasiUser
import com.bizzagi.daytrip.data.retrofit.response.Plans.Place
import com.bizzagi.daytrip.data.retrofit.response.Plans.Plan
import com.bizzagi.daytrip.data.retrofit.response.Plans.PlanPostResponse
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.response.Plans.Data

@ExperimentalCoroutinesApi
class PlansViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PlansViewModel
    private lateinit var plansRepository: PlansRepository
    private lateinit var destinationRepository: DestinationRepository
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        plansRepository = mock(PlansRepository::class.java)
        destinationRepository = mock(DestinationRepository::class.java)
        viewModel = PlansViewModel(plansRepository, destinationRepository)
        apiService = mock(ApiService::class.java)

        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchPlanIds success`() = runTest {
        val mockPlans = listOf(
            Plan(
                id = "1",
                data = mapOf("day1" to listOf("destination1", "destination2")),
                startDate = "2024-01-01",
                endDate = "2024-01-05",
                planName = "Plan A"
            ),
            Plan(
                id = "2",
                data = mapOf("day1" to listOf("destination3")),
                startDate = "2024-02-01",
                endDate = "2024-02-03",
                planName = "Plan B"
            )
        )

        `when`(plansRepository.getPlanIds()).thenReturn(mockPlans)

        viewModel.fetchPlanIds()

        val observer = mock(Observer::class.java) as Observer<List<Plan>>
        viewModel.planIds.observeForever(observer)

        verify(observer).onChanged(mockPlans)
        assertNotNull(viewModel.planIds.value)
        assertEquals(2, viewModel.planIds.value?.size)
        assertEquals("Plan A", viewModel.planIds.value?.get(0)?.planName)
        assertEquals("Plan B", viewModel.planIds.value?.get(1)?.planName)
    }

    @Test
    fun `test createPlan success`() = runTest {
        val mockSuccessResponse = PlanPostResponse(
            success = true,
            message = "Plan created successfully",
            data = Data(
                planName = "Plan A",
                startDate = "2024-01-01",
                endDate = "2024-01-05",
                data = emptyMap()
            )
        )

        val createPlanRequest = CreatePlanRequest(
            uid = "user123",
            num_days = 5,
            plan_name = "Plan A",
            lokasi_user = LokasiUser(latitude = -6.2088, longitude = 106.8456),
            places = listOf(Place("place1", -6.2088, 106.8456, 4.5, "09:00", "17:00")),
            start_date = "2024-01-01",
            end_date = "2024-01-05"
        )

        `when`(apiService.createPlan(createPlanRequest)).thenReturn(mockSuccessResponse)

        val plansRepository = PlansRepository(apiService)

        val result = plansRepository.createPlan(createPlanRequest)

        println("Result type: ${result::class.simpleName}")

        assertTrue(result is Result.Success)

        val successResult = result as Result.Success
        assertEquals("Plan A", successResult.data.data.planName)
        assertEquals("Plan created successfully", successResult.data.message)

        verify(apiService).createPlan(createPlanRequest)
    }

    @Test
    fun `test createPlan failure`() = runTest {
        val createPlanRequest = CreatePlanRequest(
            uid = "user123",
            num_days = 5,
            plan_name = "Plan D",
            lokasi_user = LokasiUser(
                latitude = -6.2088,
                longitude = 106.8456
            ),
            places = listOf(
                Place(
                    place_id = "place1",
                    latitude = -6.2088,
                    longitude = 106.8456,
                    rating = 4.5,
                    open_time = "09:00",
                    close_time = "17:00"
                )
            ),
            start_date = "2024-04-01",
            end_date = "2024-04-05"
        )

        val mockErrorResponse = PlanPostResponse(
            success = false,
            message = "Failed to create plan",
            data = Data(
                planName = "",
                startDate = "",
                endDate = "",
                data = emptyMap()
            )
        )

        val apiService = mock(ApiService::class.java)

        `when`(apiService.createPlan(createPlanRequest)).thenReturn(mockErrorResponse)

        val plansRepository = PlansRepository(apiService)

        val result = plansRepository.createPlan(createPlanRequest)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Failed to create plan", errorResult.message)

        verify(apiService).createPlan(createPlanRequest)
    }
}