package com.bizzagi.daytrip.ui.Maps.Details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        latitude = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        longitude = intent.getDoubleExtra(EXTRA_LONG, 0.0)
        destinationId = intent.getStringExtra(EXTRA_ID) ?: ""

        setupRecyclerView()


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
        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location).title("Destination"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
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