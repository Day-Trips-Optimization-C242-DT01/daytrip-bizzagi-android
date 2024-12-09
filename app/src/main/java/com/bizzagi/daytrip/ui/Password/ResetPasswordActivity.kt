package com.bizzagi.daytrip.ui.Password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bizzagi.daytrip.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnResetPassword.setOnClickListener {
            val email = binding.email.text.toString()
            if (email.isNotEmpty()) {
            }
        }
    }
}