package com.bizzagi.daytrip.ui.Trip

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.databinding.FragmentTripBinding
import com.bizzagi.daytrip.ui.Trip.Detail.DetailTripActivity
import com.bizzagi.daytrip.utils.ViewModelFactory
import kotlinx.coroutines.launch

class TripFragment : Fragment() {
    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlansViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
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

        lifecycleScope.launch {
            viewModel.fetchPlanIds()
        }

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