package com.bizzagi.daytrip.ui.Trip

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.data.retrofit.repository.PlansDummyRepository
import com.bizzagi.daytrip.databinding.FragmentTripBinding
import com.bizzagi.daytrip.ui.Trip.detail.DetailTripActivity
import com.bizzagi.daytrip.utils.ViewModelFactory

class TripFragment : Fragment() {
    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!
    //ntar dibuat instance di viewmodelfactory ya
    private lateinit var viewModel: PlansViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dihapus nnti setelah diinstance
        val repository = PlansDummyRepository// Replace with your actual repository instance
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PlansViewModel::class.java)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvTripPlan.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TripAdapter { tripId ->
            navigateToDetailTrip(tripId)
        }
        binding.rvTripPlan.adapter = adapter
    }

    private fun navigateToDetailTrip(tripId: String) {
        val intent = Intent(requireContext(), DetailTripActivity::class.java)
        intent.putExtra("TRIP_ID", tripId) // Pass the trip ID to DetailTripActivity
        startActivity(intent)
    }


    private fun observeViewModel() {
        viewModel.allTrips.observe(viewLifecycleOwner, Observer { trips ->
            val adapter = binding.rvTripPlan.adapter as TripAdapter
            adapter.submitList(trips)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}