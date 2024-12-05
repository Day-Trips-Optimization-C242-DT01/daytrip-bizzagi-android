package com.bizzagi.daytrip.ui.Trip.Edit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.MainActivity
import com.bizzagi.daytrip.data.Result
import com.bizzagi.daytrip.databinding.ActivityEditTripBinding
import com.bizzagi.daytrip.databinding.CustomDeleteDialogBinding
import com.bizzagi.daytrip.ui.Trip.Detail.DayPagerAdapter
import com.bizzagi.daytrip.ui.Trip.Detail.DestinationAdapter
import com.bizzagi.daytrip.ui.Trip.PlansViewModel
import com.bizzagi.daytrip.utils.ViewModelFactory
import com.bizzagi.daytrip.utils.showValidationDialog
import kotlinx.coroutines.launch

class EditTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTripBinding

    private lateinit var destinationsAdapter: DestinationAdapter
    private lateinit var daysAdapter: DayPagerAdapter

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

        binding.deleteTripFab.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        Log.d("EditTripActivity", "Received Trip ID: $tripId")

        setupRecyclerView(tripId)
        observeViewModel()

        viewModel.fetchDays(planId = tripId)
    }

    private fun setupRecyclerView(tripId: String) {
        daysAdapter = DayPagerAdapter { selectedDayIndex ->
            lifecycleScope.launch {
                Log.d("DetailTripActivity", "Selected day: $selectedDayIndex")
                viewModel.fetchDestinationsForDay(tripId, selectedDayIndex)
            }
        }

        binding.rvDay.apply {
            layoutManager = LinearLayoutManager(this@EditTripActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = daysAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.planDeleteResult.observe(this) { result ->
            when(result) {
                is Result.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(this,"Perjalanan berhasil dihapus", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                }
                is Result.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    Log.d("DeleteTrip", "Error: ${result.message}")
                    this.showValidationDialog("Gagal menghapus perjalanan",result.message)
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