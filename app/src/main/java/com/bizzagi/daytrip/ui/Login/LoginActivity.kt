package com.bizzagi.daytrip.ui.Login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.data.local.pref.UserModel
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.ActivityLoginBinding
import com.bizzagi.daytrip.utils.ViewModelFactory
import android.view.View
import com.bizzagi.daytrip.utils.Result
import android.app.AlertDialog
import android.content.Context
import com.bizzagi.daytrip.ui.Register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.loginLoading.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.loginLoading.visibility = View.GONE
                        showMaterialDialog(
                            context = this@LoginActivity,
                            title = "Login Failed",
                            message = result.message ?: "Login failed",
                            positiveButtonText = "Retry"
                        )
                    }
                    is Result.Success -> {
                        binding.loginLoading.visibility = View.GONE
                        if (result.data.success) {
                            val userData = result.data.data

                            val token = userData?.token ?: ""
                            val uid = userData?.uid ?: ""
                            val email = userData?.email ?: ""
                            val name = userData?.name ?: ""

                            viewModel.saveSessionData(UserModel(
                                email = email,
                                token = token,
                                isLoading = true,
                                uid = uid,
                                name = name
                            ))

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            showMaterialDialog(
                                this@LoginActivity,
                                "Login Failed",
                                result.data.message ?: "Unknown error",
                                "Retry"
                            )
                        }
                    }
                }
            }
        }

        binding.createAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showMaterialDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}