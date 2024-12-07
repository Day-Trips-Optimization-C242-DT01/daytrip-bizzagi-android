package com.bizzagi.daytrip.ui.Profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.CustomLogoutDialogBinding
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
    ): View {
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


        binding.tvLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun openEditProfile() {
        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutConfirmationDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding = CustomLogoutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            setGravity(Gravity.CENTER)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
            dialog.dismiss()
        }

        dialog.show()
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