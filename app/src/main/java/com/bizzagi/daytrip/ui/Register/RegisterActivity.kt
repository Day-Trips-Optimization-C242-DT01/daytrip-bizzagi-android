package com.bizzagi.daytrip.ui.Register

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.ActivityRegisterBinding
import com.bizzagi.daytrip.ui.Homepage.HomepageFragment
import com.bizzagi.daytrip.ui.Login.LoginActivity
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.bizzagi.daytrip.utils.Result

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
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
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val repassword = binding.confirmPasswordInput.text.toString()

            if (password.isEmpty() || repassword.isEmpty()) {
                showMaterialDialog(this@RegisterActivity, "Error", "Password cannot be empty", "OK")
                return@setOnClickListener
            }

            if (password != repassword) {
                showMaterialDialog(this@RegisterActivity, "Error", "Password and Confirm Password do not match", "OK")
                return@setOnClickListener
            }

            // Observe LiveData from ViewModel
            viewModel.register(name, email, password, repassword).observe(this) { result ->
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
                        showMaterialDialog(this@RegisterActivity, "Register Success", "Registration successful", "Ok")
                        val intent = Intent(this@RegisterActivity, HomepageFragment::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        binding.alreadyHaveAccount.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
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