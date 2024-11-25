package com.bizzagi.daytrip.ui.Profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        try {
            val existingName = sharedPreferences.getString("USER_NAME", "")
            binding.etName.setText(existingName)
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error loading name: ${e.message}")
        }

        binding.btnSave.setOnClickListener {
            val updatedName = binding.etName.text.toString().trim()
            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            with(sharedPreferences.edit()) {
                putString("USER_NAME", updatedName)
                apply()
            }
            Toast.makeText(this, "Name updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}