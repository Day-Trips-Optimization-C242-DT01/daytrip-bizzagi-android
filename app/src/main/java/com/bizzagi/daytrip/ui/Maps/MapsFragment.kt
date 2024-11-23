package com.bizzagi.daytrip.ui.Maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    // Menyimpan list marker
    private val markers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Places.initialize(requireContext(), getString(R.string.google_maps_api_key))
        placesClient = Places.createClient(requireContext())

        setupAutocomplete()

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupAutocomplete() {
        autocompleteFragment = childFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES,
            Place.Field.OPENING_HOURS
        ))

        autocompleteFragment.setHint("Cari lokasi")

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    addMarker(latLng, place.name ?: "", place.address ?: "")

                    lifecycleScope.launch {
                        getPlaceDetails(place.id)
                    }

                    clearSearchField()
                }
            }

            override fun onError(status: Status) {
                Log.e("Autocomplete", "Error: $status")
            }
        })
    }

    private fun clearSearchField() {
        autocompleteFragment.setText("")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
                    val addresses = android.location.Geocoder(requireContext())
                        .getFromLocation(latLng.latitude, latLng.longitude, 1)

                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0)

                        requireActivity().runOnUiThread {
                            addMarker(latLng, addressText, "")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Geocoding", "Error: ${e.message}")
                }
            }
        }

        // Tambahkan listener untuk klik marker
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }

        // Tambahkan tombol untuk menghapus semua marker
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

    // Tambahkan tombol untuk menghapus semua marker
    private fun addClearMarkersButton() {
        // Tambahkan ini di layout XML
        binding.btnClearMarkers.setOnClickListener {
            clearAllMarkers()
        }
    }

    // Fungsi untuk menghapus semua marker
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
}