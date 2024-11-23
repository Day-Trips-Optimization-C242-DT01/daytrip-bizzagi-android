package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.ui.Maps.MapsFragment

class AddTripActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_trip)

        if (savedInstanceState == null) {
            replaceFragment(MapsFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // fragment_container adalah id dari container di layout
            .addToBackStack(null) // opsional, untuk bisa kembali ke fragment sebelumnya
            .commit()
    }
}