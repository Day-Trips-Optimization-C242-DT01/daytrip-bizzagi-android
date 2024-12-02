package com.bizzagi.daytrip.ui.Register

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.ActivityRegisterBinding
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.bizzagi.daytrip.data.Result
import android.content.Intent

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showMaterialDialog(this@RegisterActivity, "Error", "All fields are required", "OK")
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                showMaterialDialog(this@RegisterActivity, "Invalid Email", "Please enter a valid email address", "OK")
                return@setOnClickListener
            }

            viewModel.register(name, email, password)

            viewModel.registerResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.registerLoading.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.registerLoading.visibility = View.GONE
                        showMaterialDialog(this@RegisterActivity, "Register Failed", result.message, "OK")
                    }
                    is Result.Success -> {
                        binding.registerLoading.visibility = View.GONE
                        showMaterialDialog(this@RegisterActivity, "Register Success", "Registration successful", "OK")
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
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