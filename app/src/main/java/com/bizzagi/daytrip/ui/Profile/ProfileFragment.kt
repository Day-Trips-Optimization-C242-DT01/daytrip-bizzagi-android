package com.bizzagi.daytrip.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bizzagi.daytrip.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        // Klik pada nama untuk pergi ke halaman EditProfileActivity
        binding.tvName.setOnClickListener {
            // Pastikan EditProfileActivity sudah ada di AndroidManifest.xml
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Klik pada notifikasi untuk menunjukkan pesan Toast
        binding.tvNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notification clicked!", Toast.LENGTH_SHORT).show()
        }

        // Klik pada bahasa untuk menunjukkan pesan Toast
        binding.tvLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Language clicked!", Toast.LENGTH_SHORT).show()
        }

        // Klik pada Logout untuk mengonfirmasi logout
        binding.tvLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // Fungsi untuk menampilkan konfirmasi logout
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to log out?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Proses logout
                logoutUser()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss() // Tidak melakukan apa-apa jika batal
            }
        val alert = builder.create()
        alert.show()
    }

    // Fungsi logout user (sementara dibiarkan sebagai komentar)
    private fun logoutUser() {
        // Menghapus data pengguna dari SharedPreferences atau melakukan proses logout
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        // Update nama pengguna yang ditampilkan
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val updatedName = sharedPreferences.getString("USER_NAME", "Name") ?: "Name"
        binding.tvName.text = updatedName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}