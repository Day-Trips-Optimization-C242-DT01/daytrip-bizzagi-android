package com.bizzagi.daytrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.bizzagi.daytrip.data.retrofit.response.auth.AuthenticationViewModel
import com.bizzagi.daytrip.databinding.ActivityMainBinding
import com.bizzagi.daytrip.ui.Welcome.WelcomeActivity
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastClickTime = 0L
    private val CLICK_TIME_INTERVAL = 300L

    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (user == null || user.token.isEmpty()) {
                Log.d("login kah:", "gada token bjir")
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupNavigation()
            }
        }


    }
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.navView

        navView.setOnItemSelectedListener { item ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > CLICK_TIME_INTERVAL) {
                lastClickTime = currentTime
                navController.navigate(item.itemId)
            }
            true
        }
    }
}