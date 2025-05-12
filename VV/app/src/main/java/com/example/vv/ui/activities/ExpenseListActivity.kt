package com.example.vv.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.PhotoViewerActivity
import com.example.vv.VV
import com.example.vv.R
import com.example.vv.ui.adapters.ExpenseAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var filterButton: Button
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ... existing code ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)
        filterButton = findViewById(R.id.filterButton)
        recyclerView = findViewById(R.id.expensesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set default dates (e.g., last 30 days)
        val calendar = Calendar.getInstance()
        endDateEditText.setText(dateFormat.format(calendar.time))
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        startDateEditText.setText(dateFormat.format(calendar.time))

        // Show date picker dialogs on click
        startDateEditText.setOnClickListener { showDatePickerDialog(startDateEditText) }
        endDateEditText.setOnClickListener { showDatePickerDialog(endDateEditText) }

        filterButton.setOnClickListener {
            loadExpenses()
        }

        // Load initial data
        loadExpenses()
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateStr = editText.text.toString()
        if (dateStr.isNotEmpty()) {
            try {
                val date = dateFormat.parse(dateStr)
                if (date != null) calendar.time = date
            } catch (e: Exception) {
                // ignore parse error
            }
        }

        val dpd = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun loadExpenses() {
        val startDateStr = startDateEditText.text.toString()
        val endDateStr = endDateEditText.text.toString()

        try {
            val startDate = dateFormat.parse(startDateStr)?.time ?: 0L
            // Add 1 day to endDate to include the full day
            val endDate = dateFormat.parse(endDateStr)?.time?.let { it + 24 * 60 * 60 * 1000 - 1 } ?: System.currentTimeMillis()

            lifecycleScope.launch {
                val expenses = VV.database.expenseEntryDao()
                    .getExpensesBetweenDates(startDate, endDate)

                adapter = ExpenseAdapter(expenses) { expense ->
                    if (!expense.photoUri.isNullOrEmpty()) {
                        val intent = Intent(this@ExpenseListActivity, PhotoViewerActivity::class.java)
                        intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO_URI, expense.photoUri)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ExpenseListActivity, "No photo for this expense", Toast.LENGTH_SHORT).show()
                    }
                }
                recyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
        }
    }
}
