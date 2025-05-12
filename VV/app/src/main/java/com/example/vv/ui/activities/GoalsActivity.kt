package com.example.vv.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.vv.VV
import com.example.vv.databinding.ActivityGoalSettingBinding
import com.example.vv.ui.viewmodel.BudgetViewModel
import com.example.vv.ui.viewmodel.BudgetViewModelFactory
import java.util.*

class GoalSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSettingBinding
    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory((application as VV).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe user data to pre-fill goals
        budgetViewModel.currentUserData.observe(this) { user ->
            user?.let {
                binding.etMinGoal.setText(it.minMonthlyGoal.toString())
                binding.etMaxGoal.setText(it.maxMonthlyGoal.toString())
                // If using SeekBars, update their progress
            }
        }

        binding.btnSaveGoals.setOnClickListener {
            saveGoals()
        }

        // If using SeekBars, implement listeners to update TextViews with formatted values
        // Example for a SeekBar:
        /*
        binding.seekBarMinGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val goalAmount = progress.toDouble()
                val formattedAmount = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(goalAmount)
                binding.tvMinGoalSeekBarLabel.text = "Minimum Goal: $formattedAmount"
            }
            // ... other SeekBar methods
        })
        */
    }

    private fun saveGoals() {
        val minGoalString = binding.etMinGoal.text.toString().trim()
        val maxGoalString = binding.etMaxGoal.text.toString().trim()

        val minGoal = minGoalString.toDoubleOrNull()
        val maxGoal = maxGoalString.toDoubleOrNull()

        if (minGoal == null || maxGoal == null) {
            Toast.makeText(this, "Please enter valid numbers for goals", Toast.LENGTH_SHORT).show()
            return
        }

        if (minGoal < 0 || maxGoal < 0) {
            Toast.makeText(this, "Goals cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        if (minGoal > maxGoal) {
            Toast.makeText(this, "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

        budgetViewModel.setMonthlyGoals(minGoal, maxGoal)
        Toast.makeText(this, "Monthly goals saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
