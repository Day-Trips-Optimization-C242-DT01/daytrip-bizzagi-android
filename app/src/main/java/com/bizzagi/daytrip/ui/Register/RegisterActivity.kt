package com.bizzagi.daytrip.ui.Register

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.ui.Login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val emailEditText: EditText = findViewById(R.id.email_input)
        val passwordEditText: EditText = findViewById(R.id.password_input)
        val repasswordEditText: EditText = findViewById(R.id.confirm_password_input)
        val registerButton: Button = findViewById(R.id.register_button)
        val alreadyHaveAccountTextView: TextView = findViewById(R.id.already_have_account)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repassword = repasswordEditText.text.toString()

            if (email.isBlank() || password.isBlank() || repassword.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (password != repassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                sharedPreferences.edit()
                    .putString("email", email)
                    .putString("password", password)
                    .apply()

                navigateToMainActivity()
            }
        }

        alreadyHaveAccountTextView.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() //
    }
}