package com.bizzagi.daytrip.ui.Maps

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.FragmentMapsBinding
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
@Suppress("DEPRECATION")
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private val boundsBuilder = LatLngBounds.Builder()

    private val indonesiaBounds = LatLngBounds(
        LatLng(-11.007375, 95.007307),
        LatLng(6.076912, 141.019454)
    )

    private val markerColors = listOf(
        BitmapDescriptorFactory.HUE_RED,
        BitmapDescriptorFactory.HUE_BLUE,
        BitmapDescriptorFactory.HUE_GREEN,
        BitmapDescriptorFactory.HUE_ORANGE,
        BitmapDescriptorFactory.HUE_YELLOW,
        BitmapDescriptorFactory.HUE_CYAN
    )


    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
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

        observeViewModel()
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

        mMap.setLatLngBoundsForCameraTarget(indonesiaBounds)

        mMap.setMinZoomPreference(5f)
        mMap.setMaxZoomPreference(20f)

        val indonesiaCenter = LatLng(-2.548926, 118.014863)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesiaCenter, 5f))

        mMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                val bounds = mMap.projection.visibleRegion.latLngBounds
                if (!indonesiaBounds.contains(bounds.center)) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(indonesiaBounds, 0))
                }
            }
        }

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }
    }

    private fun observeViewModel() {
        viewModel.destinationsPerDay.observe(viewLifecycleOwner) { destinationsPerDay ->
            if (destinationsPerDay.isNotEmpty()) {
                showMarkers(destinationsPerDay)
            }
        }

        viewModel.fetchDestinationsPerDay()
    }

    private fun showMarkers(destinationsPerDay: Map<String, List<DataItem>>) {
        mMap.clear()  // Clear existing markers


        var dayIndex = 0

        destinationsPerDay.forEach { (day, destinations) ->
            val color = markerColors[dayIndex % markerColors.size]
            dayIndex++

            destinations.forEach { destination ->
                val latLng = LatLng(destination.latitude, destination.longitude)
                val marker = MarkerOptions()
                    .position(latLng)
                    .title("${destination.name} (Day $day)")
                    .icon(BitmapDescriptorFactory.defaultMarker(color))

                mMap.addMarker(marker)
                boundsBuilder.include(latLng)
            }
        }

        val bounds = boundsBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }
}