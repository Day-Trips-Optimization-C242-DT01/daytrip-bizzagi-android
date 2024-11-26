package com.bizzagi.daytrip.ui.Profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.databinding.ActivityEditProfileBinding
import java.io.IOException
import android.util.Patterns

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1001
    private var selectedImageUri: Uri? = null

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

        val existingProfilePictureUri = sharedPreferences.getString("PROFILE_PICTURE", null)
        if (!existingProfilePictureUri.isNullOrEmpty()) {
            binding.profilePicture.setImageURI(Uri.parse(existingProfilePictureUri))
        }

        binding.profilePicture.setOnClickListener {
            openImagePicker()
        }

        binding.btnSave.setOnClickListener {
            val updatedName = binding.etName.text.toString().trim()
            val updatedEmail = binding.etEmail.text.toString().trim()
            val updatedPassword = binding.etPassword.text.toString().trim()

            // Validasi Nama
            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Email
            if (updatedEmail.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches()) {
                Toast.makeText(this, "Format email tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Password (Opsional)
            if (updatedPassword.isNotEmpty() && updatedPassword.length < 6) {
                Toast.makeText(this, "Password harus minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            with(sharedPreferences.edit()) {
                putString("USER_NAME", updatedName)
                putString("USER_EMAIL", updatedEmail)
                selectedImageUri?.let {
                    putString("PROFILE_PICTURE", it.toString())
                }
                apply()
            }

            Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                binding.profilePicture.setImageBitmap(bitmap)
            } catch (e: IOException) {
                Log.e("EditProfileActivity", "Error loading image: ${e.message}")
            }
        }
    }
}