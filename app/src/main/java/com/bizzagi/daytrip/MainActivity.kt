package com.bizzagi.daytrip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bizzagi.daytrip.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastClickTime = 0L
    private val CLICK_TIME_INTERVAL = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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