package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.app.DatePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.ActivityPickRegionBinding
import com.bizzagi.daytrip.utils.DateUtils
import com.google.android.material.card.MaterialCardView
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class PickRegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickRegionBinding
    private var selectedRegion: String? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var numDays: Int = 0
    private var planName: String? = null
    private var startLatitude: Double? = null
    private var startLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonDestinasi.isEnabled = false
        binding.buttonDestinasi.alpha = 0.5f

        binding.planNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                planName = s?.toString()?.trim()
                validateAllFields()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.ntb.setOnClickListener {
            updateSelectedRegion("NTB", binding.ntb)
            validateAllFields()
        }

        binding.bali.setOnClickListener {
            updateSelectedRegion("BALI", binding.bali)
            validateAllFields()
        }

        binding.lokasiUSerCard.setOnClickListener {
            val fragment = StartLocationMapsFragment()
            binding.fragmentContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonDestinasi.setOnClickListener {
            if (validateAllFields()) {
                val intent = Intent(this, AddDestinationsMapsActivity::class.java).apply {
                    putExtra(AddDestinationsMapsActivity.EXTRA_REGION, selectedRegion)
                    putExtra("EXTRA_PLAN_NAME", planName)
                    putExtra("EXTRA_START_DATE", DateUtils.formatDate(startDate!!))
                    putExtra("EXTRA_END_DATE", DateUtils.formatDate(endDate!!))
                    putExtra("EXTRA_NUM_DAYS", numDays)
                    putExtra("EXTRA_START_LATITUDE", startLatitude)
                    putExtra("EXTRA_START_LONGITUDE", startLongitude)
                }
                startActivity(intent)
            }
        }

        binding.startDateIcon.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                binding.startDateText.text = DateUtils.formatDate(date)
                validateAllFields()
            }
        }

        binding.endDateIcon.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                binding.endDateText.text = DateUtils.formatDate(date)
                validateAllFields()
            }
        }
    }

    fun onLocationSelected(latitude: Double, longitude: Double) {
        startLatitude = latitude
        startLongitude = longitude
        supportFragmentManager.popBackStack()

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        val addressText = if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            "${address.getAddressLine(0)}${if (address.locality != null) ", ${address.locality}" else ""}"
        } else {
            "Lat: $latitude, Lng: $longitude"
        }

        binding.lokasiUserText2.text = addressText
        binding.fragmentContainer.visibility = View.GONE
        validateAllFields()
    }

    private fun updateSelectedRegion(region: String, selectedCard: MaterialCardView) {
        selectedRegion = region

        resetCardVisual(binding.ntb)
        resetCardVisual(binding.bali)

        selectedCard.strokeColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        selectedCard.strokeWidth = 4
    }

    private fun resetCardVisual(card: MaterialCardView) {
        card.strokeColor = ContextCompat.getColor(this, R.color.md_theme_secondary)
        card.strokeWidth = 0
    }

    private fun showDatePicker(onDateSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateDates(): Boolean {
        if (startDate == null || endDate == null) {
            return false
        }
        if (endDate!!.before(startDate)) {
            Toast.makeText(this, "End date tidak boleh lebih awal dari start date", Toast.LENGTH_SHORT).show()
            return false
        }
        calculateNumDays()
        return true
    }

    private fun validateAllFields(): Boolean {
        val isValid = !planName.isNullOrEmpty() &&
                selectedRegion != null &&
                startDate != null &&
                endDate != null &&
                startLatitude != null &&
                startLongitude != null &&
                validateDates()

        binding.buttonDestinasi.isEnabled = isValid
        binding.buttonDestinasi.alpha = if (isValid) 1.0f else 0.5f

        return isValid
    }


    private fun calculateNumDays() {
        val startMillis = startDate!!.timeInMillis
        val endMillis = endDate!!.timeInMillis
        numDays = TimeUnit.MILLISECONDS.toDays(endMillis - startMillis).toInt() + 1
    }
}