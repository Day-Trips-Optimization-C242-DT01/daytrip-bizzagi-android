package com.bizzagi.daytrip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.data.retrofit.response.Plans.Plan
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class PlansViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PlansViewModel
    private lateinit var plansRepository: PlansRepository
    private lateinit var destinationRepository: DestinationRepository

    @Before
    fun setUp() {
        plansRepository = mock(PlansRepository::class.java)
        destinationRepository = mock(DestinationRepository::class.java)
        viewModel = PlansViewModel(plansRepository, destinationRepository)

        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchPlanIds success`() = runTest {
        // Mock the plan repository to return a list of plans
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
}