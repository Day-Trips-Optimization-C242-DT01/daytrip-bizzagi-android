package com.bizzagi.daytrip.ui.Profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.databinding.FragmnetProfileBinding

class ProfileFragment : Fragment() {

    // ViewBinding untuk fragment_profile.xml
    private var _binding: FragmnetProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout menggunakan ViewBinding
        _binding = FragmnetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle klik pada nama
        binding.tvName.setOnClickListener {
            // Navigasi ke EditProfileActivity
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle klik pada Notifikasi
        binding.tvNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notification clicked!", Toast.LENGTH_SHORT).show()
        }

        // Handle klik pada Bahasa
        binding.tvLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Language clicked!", Toast.LENGTH_SHORT).show()
        }

        // Handle klik pada Logout
        binding.tvLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        // Ambil nama terbaru dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val updatedName = sharedPreferences.getString("USER_NAME", "Name")

        // Tampilkan nama terbaru di TextView
        binding.tvName.text = updatedName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Hindari memory leaks
    }
}