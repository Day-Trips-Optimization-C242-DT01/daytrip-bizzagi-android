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
import android.widget.Toast
import com.bizzagi.daytrip.data.retrofit.model.LoginRequest
import com.bizzagi.daytrip.ui.Register.RegisterActivity
import android.text.TextWatcher
import android.text.Editable
import com.bizzagi.daytrip.ui.Password.ResetPasswordActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.createAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupAction()

        binding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
            override fun afterTextChanged(editable: Editable?) {}
        })

        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (!isValidEmail(email)) {
                showMaterialDialog(
                    this@LoginActivity,
                    "Invalid Email",
                    "Please enter a valid email address",
                    "OK"
                )
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                showMaterialDialog(this@LoginActivity, "Error", "Password cannot be empty", "OK")
                return@setOnClickListener
            }
            if (password.length < 6) {
                showMaterialDialog(this@LoginActivity, "Invalid Password", "Password must be at least 6 characters long", "OK")
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(
                email = email,
                password = password
            )

            viewModel.login(email, password)
            viewModel.loginResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.loginLoading.visibility = if (result.isLoading) View.VISIBLE else View.GONE
                    }
                    is Result.Error -> {
                        binding.loginLoading.visibility = View.GONE
                        showMaterialDialog(this@LoginActivity, "Login Failed", result.message, "OK")
                    }
                    is Result.Success -> {
                        binding.loginLoading.visibility = View.GONE
                        Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                        val loginResponse = result.data
                        viewModel.saveSessionData(loginResponse.data)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun checkFields() {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()

        binding.loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty() && password.length >= 6
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