package com.bizzagi.daytrip.ui.Maps.Details

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.ActivityDetailsMapsBinding
import com.bizzagi.daytrip.ui.Maps.MapsViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior


class DetailsMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var _binding: ActivityDetailsMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var destinationId: String = ""
    private val photoAdapter by lazy { PhotoAdapter() }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        latitude = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        longitude = intent.getDoubleExtra(EXTRA_LONG, 0.0)
        destinationId = intent.getStringExtra(EXTRA_ID) ?: ""

        setupRecyclerView()
        setupBottomSheet()

        binding.btnOpenGoogleMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
            }
        }



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeDestination()

        viewModel.getDestinationDetail(destinationId)
    }

    private fun setupRecyclerView() {
        binding.imageRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@DetailsMapsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = photoAdapter
        }
    }

    private fun observeDestination() {
        viewModel.destinationDetail.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val destination = result.data.data.firstOrNull()
                    destination?.let { updateUI(it) }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this,
                        "Error: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Loading -> {

                }
            }
        }
    }

    private fun updateUI(destination: DataItem) {
        binding.apply {
            tvName.text = destination.name
            tvRate.text = destination.rating.toString()
            tvType.text = destination.primaryType

            val openHours = if (destination.opens.isNotEmpty() && destination.closes.isNotEmpty()) {
                "Open • ${destination.opens[0]} - ${destination.closes[0]}"
            } else {
                "Opening hours not available"
            }
            tvOpenHours.text = openHours

            photoAdapter.submitList(destination.photosList)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()

        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location).title("Destination"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun setupBottomSheet() {
        val bottomSheet = findViewById<NestedScrollView>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Expand fully shows RecyclerView
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.imageRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Gradually fade in RecyclerView while sliding up
                binding.imageRecyclerView.alpha = slideOffset
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LONG = "extra_long"
        const val EXTRA_ID = "extra_id"
        private const val TAG = "DetailsMapsActivity"
    }
}