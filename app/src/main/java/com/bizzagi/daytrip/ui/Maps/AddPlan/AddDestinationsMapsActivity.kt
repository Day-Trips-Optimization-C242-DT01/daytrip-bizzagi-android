package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.local.pref.UserPreference
import com.bizzagi.daytrip.data.local.pref.dataStore
import com.bizzagi.daytrip.databinding.ActivityAddDestinationsMapsBinding
import com.bizzagi.daytrip.ui.Maps.MapsViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
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
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@Suppress("DEPRECATION")
class AddDestinationsMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val binding by lazy { ActivityAddDestinationsMapsBinding.inflate(layoutInflater) }
    private lateinit var placesClient: PlacesClient
    private lateinit var selectedRegion: String
    private lateinit var regionBounds: LatLngBounds

    private val markers = mutableListOf<Marker>()
    private val selectedDestinationsDetails = mutableListOf<com.bizzagi.daytrip.data.retrofit.response.Plans.Place>()

    private val selectedDestinations = mutableListOf<String>()
    private var mapScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var userPreference: UserPreference
    private var uid =  ""


    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        lifecycleScope.launch {
            userPreference.getSession().collect { userData ->
                if (userData != null) {
                    uid = userData.uid
                }
                Log.d("UIDTracking", "UID from UserPreference: $uid")
            }
        }


        val startLatitude = intent.getDoubleExtra("EXTRA_START_LATITUDE", 0.0)
        val startLongitude = intent.getDoubleExtra("EXTRA_START_LONGITUDE", 0.0)

        val startDateString = intent.getStringExtra("EXTRA_START_DATE")
        val endDateString = intent.getStringExtra("EXTRA_END_DATE")

        val numDays = intent.getIntExtra("EXTRA_NUM_DAYS", 1)
        val planName = intent.getStringExtra("EXTRA_PLAN_NAME")

        Log.d("AddDestinationsMaps", "Start Date String: $startDateString")
        Log.d("AddDestinationsMaps", "End Date String: $endDateString")
        Log.d("AddDestinationsMaps", "Num Days: $numDays")
        Log.d("AddDestinationsMaps", "Plan name: $planName")
        Log.d("AddDestinationsMaps", "Lat: $startLatitude Lot: $startLongitude")


        setupDestinationResultObserver()

        lifecycleScope.launch(Dispatchers.IO) {
            initializeMap()
        }

        //disini juga bisa binding push api
        lifecycleScope.launch {
            binding.buttonPlan.setOnClickListener {
                if (selectedDestinationsDetails.isEmpty()) {
                    showSnackbar("Please select at least one destination")
                    return@setOnClickListener
                }

                if (selectedDestinationsDetails.size < numDays) {
                    showSnackbar("Destinasi terlalu sedikit untuk jumlah hari yang direncanakan")
                    return@setOnClickListener
                }

                viewModel.clearPlaces()
                selectedDestinationsDetails.forEach { place ->
                    viewModel.addPlace(place)
                }

                Log.d("PostPlans", "Selected places count: ${selectedDestinationsDetails.size}")
                Log.d("PostPlans", "Places data: $selectedDestinationsDetails")

                startDateString?.let { startDate ->
                    endDateString?.let { endDate ->
                        if (planName != null) {
                            viewModel.postPlans(
                                uid = uid,
                                numDays = numDays,
                                planName = planName,
                                latitude = startLatitude,
                                longitude = startLongitude,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }
                    }
                }
            }

            setupPlanResultObserver()
        }
    }

    private suspend fun initializeMap() = withContext(Dispatchers.IO) {
        selectedRegion = intent.getStringExtra(EXTRA_REGION) ?: "BALI"
        regionBounds = REGION_BOUNDS[selectedRegion] ?: REGION_BOUNDS["BALI"]!!

        withContext(Dispatchers.Main) {
            Places.initialize(this@AddDestinationsMapsActivity, getString(R.string.google_maps_api_key))
            placesClient = Places.createClient(this@AddDestinationsMapsActivity)
            setupAutocomplete()
            setupMap()
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun isLocationInRegion(latLng: LatLng): Boolean = regionBounds.contains(latLng)

    private fun setupAutocomplete() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        REGION_BOUNDS[selectedRegion]?.let {
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(it))
        }

        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG, Place.Field.TYPES, Place.Field.OPENING_HOURS
        ))

        autocompleteFragment.setHint("Cari lokasi di $selectedRegion")
        setupAutocompletePlaceSelection(autocompleteFragment)
    }

    private fun setupAutocompletePlaceSelection(autocompleteFragment: AutocompleteSupportFragment) {
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mapScope.launch {
                    handlePlaceSelection(place)
                }
            }

            override fun onError(status: Status) {
                Log.e("Autocomplete", "Error: $status")
            }
        })
    }

    private suspend fun handlePlaceSelection(place: Place) = withContext(Dispatchers.Main) {
        place.latLng?.let { latLng ->
            if (isLocationInRegion(latLng)) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                val placeType = place.types?.firstOrNull()?.name?.replace("_", " ")?.lowercase()?.capitalize(
                    Locale.ROOT)
                val snippet = if (placeType != null) {
                    "Tipe: $placeType\n${place.address ?: ""}"
                } else {
                    place.address ?: ""
                }
                //diubah jadi nampilin detail aja
                /*addMarker(latLng, place.name ?: "", snippet)*/
            } else {
                showSnackbar("Lokasi harus berada di wilayah $selectedRegion")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapScope.launch {
            setupMapConfiguration()
            setupMapListeners()
        }
      /*  binding.buttonPlan.setOnClickListener {
            viewModel.postPlans(

            )
        }*/
    }

    private suspend fun setupMapConfiguration() = withContext(Dispatchers.Main) {
        mMap.apply {
            setLatLngBoundsForCameraTarget(regionBounds)
            setupZoomPreferences()

            val centerPosition = REGION_CENTERS[selectedRegion] ?: REGION_CENTERS["BALI"]!!
            moveCamera(CameraUpdateFactory.newLatLngZoom(centerPosition, 9f))

            uiSettings.apply {
                isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
                isMapToolbarEnabled = true
            }
        }
    }

    private fun setupMapListeners() {
        mMap.apply {
            setOnCameraMoveListener {
                mapScope.launch {
                    handleCameraMove()
                }
            }

            setOnPoiClickListener { pointOfInterest ->
                mapScope.launch {
                    handlePoiClick(pointOfInterest)
                }
            }

            setOnMapClickListener { latLng ->
                mapScope.launch {
                    handleMapClick(latLng)
                }
            }
        }

        addClearMarkersButton()
    }

    private suspend fun handleCameraMove() = withContext(Dispatchers.Main) {
        val currentPosition = mMap.cameraPosition.target
        if (!isLocationInRegion(currentPosition)) {
            val centerPosition = REGION_CENTERS[selectedRegion] ?: REGION_CENTERS["BALI"]!!
            mMap.moveCamera(CameraUpdateFactory.newLatLng(centerPosition))
            showSnackbar("Wilayah berada diluar batas wilayah yang ditentukan")
        }
    }

    private suspend fun handlePoiClick(pointOfInterest: PointOfInterest) = withContext(Dispatchers.Main) {
        try {
            val placeFields = listOf(Place.Field.ID, Place.Field.TYPES)
            val request = FetchPlaceRequest.newInstance(pointOfInterest.placeId, placeFields)
            val placeResponse = placesClient.fetchPlace(request).await()

            val allowedTypes = setOf(
                Place.Type.TOURIST_ATTRACTION,
                Place.Type.AMUSEMENT_PARK,
                Place.Type.ZOO,
                Place.Type.MUSEUM,
                Place.Type.PARK,
                Place.Type.AQUARIUM,
                Place.Type.ART_GALLERY,
                Place.Type.NATURAL_FEATURE
            )

            if (placeResponse.place.types?.any { it in allowedTypes } == true) {
                withContext(Dispatchers.Main) {
                    addMarker(pointOfInterest.latLng, pointOfInterest.name, "")
                }
                selectedDestinations.add(pointOfInterest.placeId)
                setupDestinationResultObserver()
                getPlaceDetails(pointOfInterest.placeId)

                withContext(Dispatchers.Main) {
                    viewModel.postDestination(pointOfInterest.placeId)
                }
            } else {
                withContext(Dispatchers.Main) {
                    showSnackbar("Destinasi ini bukan objek wisata utama $selectedRegion")
                }
            }
        } catch (e: Exception) {
            Log.e("PlaceDetails", "Error fetching place details: ${e.message}")
        }
    }

    private fun setupDestinationResultObserver() {
        viewModel.destinationResult.observe(this) {result ->
            Log.d("DestinationAdd","result $result")
            when (result) {
                is Result.Loading -> {
                    Log.d("DestinationAdd","loading ")
                }
                is Result.Success -> {
                    Log.d("DestinationAdd","Success $result")
                }
                is Result.Error -> {
                    Log.d("DestinationAdd", "Error: $result.error")

                }
            }
        }
    }

    private suspend fun handleMapClick(latLng: LatLng) {
        withContext(Dispatchers.IO) {
            val addresses = Geocoder(this@AddDestinationsMapsActivity)
                .getFromLocation(latLng.latitude, latLng.longitude, 1)

            withContext(Dispatchers.Main) {
                if (!addresses.isNullOrEmpty()) {
                    val addressText = addresses[0].getAddressLine(0)
                    // Gunakan data di UI
                }
            }
        }
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

    private fun setupPlanResultObserver() {
        viewModel.planResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.buttonPlan.isEnabled = false
                    showSnackbar("Creating plan...")
                }
                is Result.Success -> {
                    val plan = result.data
                    val days = plan.data.data
                    binding.buttonPlan.isEnabled = true
                    showSnackbar("Berhasil membuat plan!")
                    Log.d("PostPlans", "Success response: ${result.data}")
                    days.forEach { (day, places) ->
                        Log.d("PostPlans", "Day: $day, Places: $places")
                    }
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    binding.buttonPlan.isEnabled = true
                    showSnackbar("Error creating plan")
                    Log.e("PostPlans", "Error response: ${result}")
                }
            }
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

        selectedDestinations.clear()
        Log.d("SelectedDestinations", "Cleared. Current IDs: $selectedDestinations")
    }

    private suspend fun getPlaceDetails(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.RATING,
            Place.Field.OPENING_HOURS
        )
        try {
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            val placeResponse = placesClient.fetchPlace(request).await()
            val place = placeResponse.place

            val latLng = place.latLng
            val rating: Double? = place.rating
            val openingHours = place.openingHours?.weekdayText

            val openTime = mutableListOf<String>()
            val closeTime = mutableListOf<String>()

            if (openingHours != null) {
                for (day in openingHours) {
                    val hours = day.split(": ", limit = 2).getOrNull(1)
                    if (hours != null) {
                        val times = hours.split("–")
                        openTime.add(times.getOrNull(0)?.trim() ?: "00:00")
                        closeTime.add(times.getOrNull(1)?.trim() ?: "00:00")
                    } else {
                        openTime.add("00:00")
                        closeTime.add("00:00")
                    }
                }
            } else {
                repeat(7) {
                    openTime.add("00:00")
                    closeTime.add("00:00")
                }
            }

            if (latLng != null) {
                val detail = com.bizzagi.daytrip.data.retrofit.response.Plans.Place(
                    place_id = place.id ?: "",
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    rating = rating ?: 0.0,
                    open_time = openTime.firstOrNull() ?: "00:00",
                    close_time = closeTime.firstOrNull() ?: "00:00"
                )
                selectedDestinationsDetails.add(detail)
                Log.d("SelectedDestinationsDetails", "Added: $detail")
                logSelectedDestinations()
            }
        } catch (e: Exception) {
            Log.e("PlaceDetails", "Error fetching place details: ${e.message}")
        }
    }



    private fun logSelectedDestinations() {
        Log.d("SelectedDestinations", "Total selected: ${selectedDestinationsDetails.size}")
        selectedDestinationsDetails.forEach { placeDetail ->
            Log.d("SelectedDestinations", """
            Place ID: ${placeDetail.place_id}
            Latitude: ${placeDetail.latitude}
            Longitude: ${placeDetail.longitude}
            Rating: ${placeDetail.rating ?: "N/A"}
            Open Time: ${placeDetail.open_time}
            Close Time: ${placeDetail.close_time}
        """.trimIndent())
        }
    }


    private fun setupZoomPreferences() {
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
    }

    private fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.md_theme_primary))
            .setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.m3_ref_palette_white))
            .setAnchorView(binding.btnClearMarkers)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
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