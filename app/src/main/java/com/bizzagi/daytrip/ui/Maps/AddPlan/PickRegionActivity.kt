package com.bizzagi.daytrip.ui.Maps.AddPlan

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.ActivityPickRegionBinding
import com.bizzagi.daytrip.utils.DateUtils
import com.google.android.material.card.MaterialCardView
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PickRegionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickRegionBinding
    private var selectedRegion: String? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var numDays: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ntb.setOnClickListener {
            updateSelectedRegion("NTB", binding.ntb)
        }

        binding.bali.setOnClickListener {
            updateSelectedRegion("BALI", binding.bali)
        }

        binding.buttonDestinasi.setOnClickListener {
            if (selectedRegion != null) {
                if (validateDates()) {
                    val intent = Intent(this, AddDestinationsMapsActivity::class.java).apply {
                        putExtra(AddDestinationsMapsActivity.EXTRA_REGION, selectedRegion)
                      /*  putExtra("EXTRA_START_DATE", startDate?.timeInMillis)
                        putExtra("EXTRA_END_DATE", endDate?.timeInMillis)
                        putExtra("EXTRA_NUM_DAYS", numDays)*/
                    }
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Pilih region terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        binding.startDateIcon.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                binding.startDateText.text = DateUtils.formatDate(date)
            }
        }

        binding.endDateIcon.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                binding.endDateText.text = DateUtils.formatDate(date)
            }
        }
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
            Toast.makeText(this, "Pilih start date dan end date", Toast.LENGTH_SHORT).show()
            return false
        }
        if (endDate!!.before(startDate)) {
            Toast.makeText(this, "End date tidak boleh lebih awal dari start date", Toast.LENGTH_SHORT).show()
            return false
        }
        calculateNumDays()
        return true
    }

    private fun calculateNumDays() {
        val startMillis = startDate!!.timeInMillis
        val endMillis = endDate!!.timeInMillis
        numDays = TimeUnit.MILLISECONDS.toDays(endMillis - startMillis).toInt()
    }
}
