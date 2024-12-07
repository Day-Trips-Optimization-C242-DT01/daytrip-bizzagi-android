package com.bizzagi.daytrip.ui.Trip.Edit

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.data.retrofit.response.Destinations.DataItem
import com.bizzagi.daytrip.databinding.ActivityEditTripBinding
import com.bizzagi.daytrip.databinding.CustomDeleteDialogBinding
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory

class EditTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTripBinding

    private lateinit var editAdapter: EditAdapter

    private var currentDestinationsMap = mutableMapOf<String, List<DataItem>>()

    private val viewModel by viewModels <PlansViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getStringExtra("TRIP_ID")
        if (tripId == null) {
            Toast.makeText(this, "Plan ID error", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnHapus.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.editTripFab.setOnClickListener {
            if (validateDestinations()) {
                saveChanges()
            } else {
                Toast.makeText(this, "Each day must have at least one destination", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("EditTripActivity", "Received Trip ID: $tripId")

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchDays(planId = tripId)
    }

    private fun validateDestinations(): Boolean {
        return currentDestinationsMap.values.all { it.isNotEmpty() }
    }


    private fun setupRecyclerView() {
        editAdapter = EditAdapter()
        editAdapter.setOnDaysUpdatedListener { updatedMap ->
            currentDestinationsMap = updatedMap.toMutableMap()
        }

        binding.rvDays.apply {
            layoutManager = LinearLayoutManager(this@EditTripActivity)
            adapter = editAdapter
        }
    }

    private fun saveChanges() {
        val tripId = intent.getStringExtra("TRIP_ID") ?: return

        val updatedData = currentDestinationsMap.mapValues { (_, destinations) ->
            destinations.map { it.id }
        }

        viewModel.updatePlan(tripId, updatedData)
    }

    private fun observeViewModel() {
        viewModel.days.observe(this) { daysMap ->
            daysMap.forEach { (day, destinationIds) ->
                viewModel.fetchDestinationsEditForDay(
                    intent.getStringExtra("TRIP_ID") ?: return@forEach,
                    day
                )
            }
        }

        viewModel.destinationsPerDay.observe(this) { destinationsMap ->
            val daysMap = viewModel.days.value ?: return@observe

            val dayDestinationPairs = daysMap.map { (day, _) ->
                day to (destinationsMap[day] ?: emptyList())
            }.sortedBy { it.first }

            editAdapter.submitData(dayDestinationPairs)
        }

        viewModel.updatePlanResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "Plan updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to update plan: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // handle loading
                }
            }
        }
    }


    private fun showDeleteConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding = CustomDeleteDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            setGravity(Gravity.CENTER)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnLogout.setOnClickListener {
            intent.getStringExtra("TRIP_ID")?.let { id ->
                viewModel.deletePlan(id)
                Log.d("DeleteTrip", "Deleting trip with ID: $id")
                dialog.dismiss()
                finish()
            } ?: run {
                Toast.makeText(this, "Trip ID not found", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

}