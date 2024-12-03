package com.bizzagi.daytrip.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.FragmentProfileBinding
import com.bizzagi.daytrip.ui.Welcome.WelcomeActivity
import com.bizzagi.daytrip.utils.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                binding.tvName.text = result.name
            }
        }

        binding.tvName.setOnClickListener {
            openEditProfile()
        }

        binding.profileImage.setOnClickListener {
            openEditProfile()
        }

        binding.tvNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notification clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.tvLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Language clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.tvLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun openEditProfile() {
        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to log out?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                logoutUser()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun logoutUser() {
        viewModel.logout()
        Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val updatedName = sharedPreferences.getString("USER_NAME", "Name") ?: "Name"
        binding.tvName.text = updatedName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}