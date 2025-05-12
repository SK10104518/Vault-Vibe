package com.example.vv.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vv.VV
import com.example.vv.data.db.entities.Category
import com.example.vv.databinding.ActivityReportsBinding
import com.example.vv.ui.adapters.CategorySpendingAdapter
import com.example.vv.ui.adapters.ExpenseAdapter
import com.example.vv.ui.viewmodel.BudgetViewModel
import com.example.vv.ui.viewmodel.BudgetViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory((application as VV).repository)
    }

    private var selectedStartDateMillis: Long = 0
    private var selectedEndDateMillis: Long = 0

    private lateinit var filteredExpenseAdapter: ExpenseAdapter
    private lateinit var categorySpendingAdapter: CategorySpendingAdapter

    private var categories: List<Category> = emptyList() // To map category IDs to names

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerViews
        filteredExpenseAdapter = ExpenseAdapter(
            onExpenseClick = { expense ->
                // Handle click on filtered expense (e.g., show photo dialog)
                if (!expense.photoUri.isNullOrEmpty()) {
                    // Re-use the showPhotoDialog from MainActivity
                    // You might need to make it a utility function or pass context
                    // For simplicity here, we'll just show a toast
                    Toast.makeText(this, "Clicked on expense: ${expense.description}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No photo for this expense", Toast.LENGTH_SHORT).show()
                }
            },
            getCategoryName = { categoryId ->
                categories.find { it.id == categoryId }?.name ?: "Unknown Category"
            }
        )
        binding.rvFilteredExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvFilteredExpenses.adapter = filteredExpenseAdapter

        categorySpendingAdapter = CategorySpendingAdapter(
            getCategoryName = { categoryId ->
                categories.find { it.id == categoryId }?.name ?: "Unknown Category"
            }
        )
        binding.rvCategorySpending.layoutManager = LinearLayoutManager(this)
        binding.rvCategorySpending.adapter = categorySpendingAdapter

        // Observe categories to map IDs to names for both adapters
        budgetViewModel.allCategories.observe(this) { categoriesList ->
            categories = categoriesList
            // Trigger data update for adapters if needed
            // You might want to re-apply the filter or update lists manually
        }

        // Observe filtered expenses
        budgetViewModel.getExpensesInDateRange(selectedStartDateMillis, selectedEndDateMillis) // Initial observation
            .observe(this) { expenses ->
                filteredExpenseAdapter.submitList(expenses)
            }

        // Observe category spending
        budgetViewModel.getTotalSpendingByCategory(selectedStartDateMillis, selectedEndDateMillis) // Initial observation
            .observe(this) { categorySpending ->
                categorySpendingAdapter.submitList(categorySpending)
            }


        binding.btnPickStartDate.setOnClickListener { showDatePicker(true) }
        binding.btnPickEndDate.setOnClickListener { showDatePicker(false) }
        binding.btnApplyFilter.setOnClickListener { applyFilter() }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            val selectedDate = calendar.timeInMillis
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(selectedDate))

            if (isStartDate) {
                selectedStartDateMillis = selectedDate
                binding.tvSelectedStartDate.text = formattedDate
            } else {
                selectedEndDateMillis = selectedDate
                binding.tvSelectedEndDate.text = formattedDate
            }
        }, year, month, day).show()
    }

    private fun applyFilter() {
        if (selectedStartDateMillis == 0L || selectedEndDateMillis == 0L) {
            Toast.makeText(this, "Please select a start and end date", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartDateMillis > selectedEndDateMillis) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
            return
        }

        // The LiveData observations will automatically update the lists
        // when the date range changes in the ViewModel query.
        // We just need to trigger the initial observation or ensure
        // the ViewModel exposes methods that react to date range changes.

        // A better approach would be to have filter parameters in the ViewModel
        // and update them, which would trigger the LiveData.

        // For now, let's re-observe or call a function in ViewModel to filter
        // Note: This is a simplified approach. A more robust solution
        // involves passing filter parameters to the ViewModel or Repository.
        // For demonstration, let's assume re-observing is sufficient for now.

        // To make the LiveData in ViewModel reactive to date changes,
        // you could use something like `switchMap` or pass the dates to the repository
        // when they change.

        // Example of re-observing (less efficient for frequent changes):
        budgetViewModel.getExpensesInDateRange(selectedStartDateMillis, selectedEndDateMillis)
            .observe(this) { expenses ->
                filteredExpenseAdapter.submitList(expenses)
            }

        budgetViewModel.getTotalSpendingByCategory(selectedStartDateMillis, selectedEndDateMillis)
            .observe(this) { categorySpending ->
                categorySpendingAdapter.submitList(categorySpending)
            }

        Toast.makeText(this, "Filter applied", Toast.LENGTH_SHORT).show()
    }
}
