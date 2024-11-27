package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.databinding.ActivityPickRegionBinding

class PickRegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickRegionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ntb.setOnClickListener {
            val intent = Intent(this, AddDestinationsMapsActivity::class.java).apply {
                putExtra(AddDestinationsMapsActivity.EXTRA_REGION, "NTB")
            }
            startActivity(intent)
        }

        binding.bali.setOnClickListener {
            val intent = Intent(this, AddDestinationsMapsActivity::class.java).apply {
                putExtra(AddDestinationsMapsActivity.EXTRA_REGION, "BALI")
            }
            startActivity(intent)
        }
    }
}