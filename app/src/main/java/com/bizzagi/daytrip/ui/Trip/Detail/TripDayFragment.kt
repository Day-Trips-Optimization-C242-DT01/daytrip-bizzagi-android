package com.bizzagi.daytrip.ui.Trip.Detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.data.retrofit.response.DestinationDummy
import com.bizzagi.daytrip.databinding.FragmentTripDayBinding
import com.bizzagi.daytrip.ui.Trip.PlansViewModel

class TripDayFragment : Fragment() {
    private var _binding: FragmentTripDayBinding? = null
    private val binding get() = _binding!!

    // Tambahkan viewModel
    private lateinit var viewModel: PlansViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDayBinding.inflate(inflater, container, false)

        // Dapatkan viewModel dari activity
        viewModel = ViewModelProvider(requireActivity())[PlansViewModel::class.java]

        val dayNumber = arguments?.getInt("day") ?: 1


        // Ambil destinasi untuk hari ini
        val destinations = viewModel.getDestinationsForDay(dayNumber - 1)

        // Setup RecyclerView untuk menampilkan destinasi
        setupDestinationsList(destinations)

        return binding.root
    }

    private fun setupDestinationsList(destinations: List<DestinationDummy>) {
        // Buat adapter untuk RecyclerView
        val adapter = DestinationAdapter()
        binding.destinationRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        // Berikan data ke adapter
        adapter.submitList(destinations)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}