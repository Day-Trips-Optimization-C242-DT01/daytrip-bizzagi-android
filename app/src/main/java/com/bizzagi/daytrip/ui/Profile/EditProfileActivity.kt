package com.bizzagi.daytrip.ui.Profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    // Deklarasi binding untuk ViewBinding
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load existing name from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        try {
            // Set nilai awal ke EditText
            val existingName = sharedPreferences.getString("USER_NAME", "")
            binding.etName.setText(existingName)
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error loading name: ${e.message}")
        }

        // Save updated name to SharedPreferences
        binding.btnSave.setOnClickListener {
            val updatedName = binding.etName.text.toString().trim()
            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan nama baru ke SharedPreferences
            with(sharedPreferences.edit()) {
                putString("USER_NAME", updatedName)
                apply()
            }
            Toast.makeText(this, "Name updated successfully!", Toast.LENGTH_SHORT).show()
            finish() // Tutup activity dan kembali ke sebelumnya
        }
    }
}