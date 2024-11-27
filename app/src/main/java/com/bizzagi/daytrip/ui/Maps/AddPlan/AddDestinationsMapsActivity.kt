package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.ActivityAddDestinationsMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class AddDestinationsMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddDestinationsMapsBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var selectedRegion: String
    private lateinit var regionBounds: LatLngBounds

    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDestinationsMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedRegion = intent.getStringExtra(EXTRA_REGION) ?: "BALI"
        regionBounds = REGION_BOUNDS[selectedRegion] ?: REGION_BOUNDS["BALI"]!!

        Places.initialize(this, getString(R.string.google_maps_api_key))
        placesClient = Places.createClient(this)

        setupAutocomplete()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun isLocationInRegion(latLng: LatLng): Boolean {
        return regionBounds.contains(latLng)
    }

    private fun setupAutocomplete() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        val bounds = REGION_BOUNDS[selectedRegion]
        bounds?.let {
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(bounds))
        }

        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES,
            Place.Field.OPENING_HOURS
        ))

        autocompleteFragment.setHint("Cari lokasi di $selectedRegion")

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    if (isLocationInRegion(latLng)) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        addMarker(latLng, place.name ?: "", place.address ?: "")
                    } else {
                        Toast.makeText(
                            this@AddDestinationsMapsActivity,
                            "Lokasi harus berada di wilayah $selectedRegion",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onError(status: Status) {
                Log.e("Autocomplete", "Error: $status")
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setLatLngBoundsForCameraTarget(regionBounds)

        when (selectedRegion) {
            "NTB" -> {
                mMap.setMinZoomPreference(8f)
                mMap.setMaxZoomPreference(20f)
            }
            "BALI" -> {
                mMap.setMinZoomPreference(9f)
                mMap.setMaxZoomPreference(20f)
            }
        }

        mMap.setMinZoomPreference(5f)
        mMap.setMaxZoomPreference(20f)

        val centerPosition = REGION_CENTERS[selectedRegion] ?: REGION_CENTERS["BALI"]!!
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPosition, 9f))

        mMap.setOnCameraMoveListener {
            val currentPosition = mMap.cameraPosition.target
            if (!isLocationInRegion(currentPosition)) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(centerPosition))
                Toast.makeText(
                    this@AddDestinationsMapsActivity,
                    "Wilayah berada diluar batas wilayah $selectedRegion",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mMap.setOnPoiClickListener { pointOfInterest ->
            addMarker(
                pointOfInterest.latLng,
                pointOfInterest.name,
                ""
            )

            lifecycleScope.launch {
                getPlaceDetails(pointOfInterest.placeId)
            }
        }

        mMap.setOnMapClickListener { latLng ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val addresses = android.location.Geocoder(this@AddDestinationsMapsActivity)
                        .getFromLocation(latLng.latitude, latLng.longitude, 1)

                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0)

                        runOnUiThread {
                            addMarker(latLng, addressText, "")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Geocoding", "Error: ${e.message}")
                }
            }
        }

        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }

        addClearMarkersButton()
    }

    private fun addMarker(latLng: LatLng, title: String, snippet: String) {
        val marker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(markers.size)))
        )

        marker?.let {
            markers.add(it)
            it.showInfoWindow()
        }
    }

    private fun getMarkerColor(index: Int): Float {
        val colors = listOf(
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_AZURE
        )
        return colors[index % colors.size]
    }

    private fun addClearMarkersButton() {
        binding.btnClearMarkers.setOnClickListener {
            clearAllMarkers()
        }
    }

    private fun clearAllMarkers() {
        markers.forEach { it.remove() }
        markers.clear()
    }

    private suspend fun getPlaceDetails(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES,
            Place.Field.OPENING_HOURS
        )
        try {
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            val placeResponse = placesClient.fetchPlace(request).await()
            val place = placeResponse.place

            val name = place.name
            val address = place.address
            val latLng = place.latLng
            val types = place.types
            val openingHours = place.openingHours?.weekdayText?.joinToString(", ")

            Log.d("PlaceDetails", "Name: $name")
            Log.d("PlaceDetails", "Address: $address")
            Log.d("PlaceDetails", "LatLng: $latLng")
            Log.d("PlaceDetails", "Types: $types")
            Log.d("PlaceDetails", "Opening Hours: $openingHours")
        } catch (e: Exception) {
            Log.e("PlaceDetails", "Error fetching place details: ${e.message}")
        }
    }

    companion object {
        const val EXTRA_REGION = "extra_region"

        private val REGION_BOUNDS = mapOf(
            "NTB" to LatLngBounds(
                LatLng(-9.219741, 115.667416),
                LatLng(-8.002291, 119.348972)
            ),
            "BALI" to LatLngBounds(
                LatLng(-8.930736, 114.432539),
                LatLng(-8.060337, 115.711412)
            )
        )

        private val REGION_CENTERS = mapOf(
            "NTB" to LatLng(-8.611016, 117.508194),
            "BALI" to LatLng(-8.495537, 115.071976)
        )
    }
}