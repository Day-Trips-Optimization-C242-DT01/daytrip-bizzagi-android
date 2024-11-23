package com.bizzagi.daytrip.ui.Homepage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.FragmentHomepageBinding
import com.bizzagi.daytrip.ui.Maps.AddPlan.PickRegionActivity


class HomepageFragment : Fragment() {
    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentHomepageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTripFab.setOnClickListener {
            val intent = Intent(requireContext(),PickRegionActivity::class.java)
            startActivity(intent)
        }
    }

}