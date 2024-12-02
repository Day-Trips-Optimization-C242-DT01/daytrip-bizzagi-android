
package com.bizzagi.daytrip.ui.Login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.ActivityLoginBinding
import com.bizzagi.daytrip.utils.ViewModelFactory
import android.view.View
import com.bizzagi.daytrip.data.Result
import android.app.AlertDialog
import android.content.Context
import com.bizzagi.daytrip.data.retrofit.model.LoginRequest


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (!isValidEmail(email)) {
                showMaterialDialog(this@LoginActivity, "Invalid Email", "Please enter a valid email address", "OK")
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                showMaterialDialog(this@LoginActivity, "Error", "Password cannot be empty", "OK")
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(
                email = email,
                password = password
            )

            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.loginLoading.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.loginLoading.visibility = View.GONE
                        showMaterialDialog(this@LoginActivity, "Login Failed", result.message ?: "An error occurred", "OK")
                    }
                    is Result.Success -> {
                        binding.loginLoading.visibility = View.GONE
                        showMaterialDialog(this@LoginActivity, "Login Success", "Login successful", "OK")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zAZ0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    private fun showMaterialDialog(context: Context, title: String, message: String, buttonText: String) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }
}