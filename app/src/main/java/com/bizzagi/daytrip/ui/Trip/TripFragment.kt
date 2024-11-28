package com.bizzagi.daytrip.ui.Trip

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.data.retrofit.ApiConfig
import com.bizzagi.daytrip.data.retrofit.repository.DestinationRepository
import com.bizzagi.daytrip.data.retrofit.repository.PlansRepository
import com.bizzagi.daytrip.databinding.FragmentTripBinding
import com.bizzagi.daytrip.ui.Trip.Detail.DetailTripActivity
import com.bizzagi.daytrip.utils.ViewModelFactory

class TripFragment : Fragment() {
    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!
    //ntar dibuat instance di viewmodelfactory ya
    private lateinit var viewModel: PlansViewModel
    private lateinit var tripAdapter: TripAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiConfig.getApiService()

        // Create the repository first
        val repository = PlansRepository(DestinationRepository(apiService)) // Create repository instance

        // Create factory with repository
        //change injection soon
        val factory = ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(PlansViewModel::class.java)

        viewModel.fetchPlans()

        setupRecyclerView()
        observeViewModel()
    }
    private fun setupRecyclerView() {
        tripAdapter = TripAdapter { tripId ->
            navigateToDetailTrip(tripId)
        }
        binding.rvTripPlan.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripAdapter
        }
    }

    private fun navigateToDetailTrip(tripId: String) {
        val intent = Intent(requireContext(), DetailTripActivity::class.java)
        intent.putExtra("TRIP_ID", tripId)
        startActivity(intent)
    }


    private fun observeViewModel() {
        viewModel.planIds.observe(viewLifecycleOwner) { plans ->
            tripAdapter.submitList(plans)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}