package com.bizzagi.daytrip.ui.Login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.R
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
        // Setup tampilan (misalnya untuk menghilangkan status bar)
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Mengambil email dan password dari EditText
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
                            // Mengambil informasi pengguna dari hasil login
                            val userData = result.data.data

                            // Mendapatkan token, uid, email, dan nama
                            val token = userData?.token ?: ""  // Jika token kosong, beri nilai default
                            val uid = userData?.uid ?: ""      // Jika uid kosong, beri nilai default
                            val email = userData?.email ?: ""  // Jika email kosong, beri nilai default
                            val name = userData?.name ?: ""

                            viewModel.saveSessionData(UserModel(
                                email = email,
                                token = token,  // Pastikan token valid (String)
                                isLoading = true,  // Set ke true karena login berhasil
                                uid = uid,
                                name = name
                            ))

                            // Berpindah ke MainActivity setelah login berhasil
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
            // Navigasi ke activity registrasi
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
        // Menampilkan dialog dengan judul dan pesan
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}