package com.bizzagi.daytrip.ui.Trip.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.FragmentTripDayBinding

class TripDayFragment : Fragment() {
    private var _binding: FragmentTripDayBinding? = null
    private val binding get() = _binding!!
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val binding = FragmentTripDayBinding.inflate(inflater,container,false)

        val dayNumber = arguments?.getInt("day") ?: 1
        binding.dayNumberTextView.text = "Hari $dayNumber"
        return binding.root
    }
}