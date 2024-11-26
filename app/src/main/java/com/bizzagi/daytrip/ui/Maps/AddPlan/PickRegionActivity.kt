package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.ActivityPickRegionBinding

class PickRegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickRegionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ntb.setOnClickListener {
            val intent = Intent(this,AddDestinationsMapsActivity::class.java)
            startActivity(intent)
        }
    }
}