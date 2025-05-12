package com.example.vv.ui.activities//package com.example.budgettracker

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.example.vv.VV
import com.example.vv.data.db.entities.Category
import com.example.vv.data.db.entities.Expense
import com.example.vv.databinding.ActivityAddExpenseBinding
import com.example.vv.ui.viewmodel.BudgetViewModel
import com.example.vv.ui.viewmodel.BudgetViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory((application as VV).repository)
    }
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var selectedDateMillis: Long = 0
    private var selectedStartTime: String = ""
    private var selectedEndTime: String = ""
    private var selectedCategoryId: Int = -1 // Store the selected category ID
    private var selectedPhotoUri: Uri? = null

    // Activity Result Launcher for picking an image from the gallery
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedPhotoUri = uri
                // Display the selected image
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivExpensePhoto)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Populate category spinner
        budgetViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(
                this,
                R.layout.simple_spinner_dropdown_item,
                categoryNames
            )
            binding.spinnerCategory.adapter = adapter

            // Handle category selection
            binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategoryId = categories[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategoryId = -1 // Indicate no category selected
                }

            }
        }

        binding.btnPickDate.setOnClickListener { showDatePicker() }
        binding.btnPickStartTime.setOnClickListener { showTimePicker(true) }
        binding.btnPickEndTime.setOnClickListener { showTimePicker(false) }
        binding.btnPickPhoto.setOnClickListener { pickImage.launch("image/*") }
        binding.btnSaveExpense.setOnClickListener { saveExpense() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            selectedDateMillis = calendar.timeInMillis
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvSelectedDate.text = dateFormat.format(Date(selectedDateMillis))
        }, year, month, day).show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, h, m ->
            val time = String.format("%02d:%02d", h, m)
            if (isStartTime) {
                selectedStartTime = time
                binding.tvSelectedStartTime.text = time
            } else {
                selectedEndTime = time
                binding.tvSelectedEndTime.text = time
            }
        }, hour, minute, false).show()
    }

    private fun saveExpense() {
        val description = binding.etDescription.text.toString().trim()
        val amountString = binding.etAmount.text.toString().trim()
        val amount = amountString.toDoubleOrNull()

        if (description.isEmpty() || selectedDateMillis == 0L || selectedStartTime.isEmpty() || selectedEndTime.isEmpty() || amount == null || selectedCategoryId == -1) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            userId = userId,
            description = description,
            date = selectedDateMillis,
            startTime = selectedStartTime,
            endTime = selectedEndTime,
            categoryId = selectedCategoryId,
            amount = amount,
            photoUri = null // Will be updated after photo upload
        )

        // Upload photo first if selected
        if (selectedPhotoUri != null) {
            budgetViewModel.uploadExpensePhoto(selectedPhotoUri!!, expense) { photoUrl ->
                if (photoUrl != null) {
                    Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    // You might want to still save the expense without the photo
                }
            }
        } else {
            // Save expense directly if no photo is selected
            budgetViewModel.insertExpense(expense)
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
