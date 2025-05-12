package com.example.vv

import android.content.Intent
import com.example.vv.ui.activities.LoginActivity
import com.example.vv.databinding.ActivityMainBinding
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vv.data.db.entities.Category
import com.example.vv.ui.activities.AddExpenseActivity
import com.example.vv.ui.activities.CategoryActivity
import com.example.vv.ui.activities.GoalSettingActivity
import com.example.vv.ui.activities.ReportsActivity
import com.example.vv.ui.adapters.ExpenseAdapter
import com.example.vv.ui.viewmodel.BudgetViewModel
import com.example.vv.ui.viewmodel.BudgetViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory((application as VV).repository)
    }

    private lateinit var expenseAdapter: ExpenseAdapter
    private var categories: List<Category> = emptyList() // To map category IDs to names

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Check if user is logged in (should be handled by LoginActivity, but good practice)
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Setup RecyclerView
        expenseAdapter = ExpenseAdapter(
            onExpenseClick = { expense ->
                // Handle expense item click (e.g., show full photo)
                if (!expense.photoUri.isNullOrEmpty()) {
                    showPhotoDialog(expense.photoUri)
                } else {
                    Toast.makeText(this, "No photo for this expense", Toast.LENGTH_SHORT).show()
                }
            },
            getCategoryName = { categoryId ->
                categories.find { it.id == categoryId }?.name ?: "Unknown Category"
            }
        )
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = expenseAdapter

        // Observe categories to map IDs to names
        budgetViewModel.allCategories.observe(this) { categoriesList ->
            categories = categoriesList
            // Update the expense list when categories are loaded/changed
            // This is needed because the adapter uses the category names
            budgetViewModel.allExpenses.value?.let { expenseAdapter.submitList(it) }
        }

        // Observe expenses and update the RecyclerView
        budgetViewModel.allExpenses.observe(this) { expenses ->
            expenseAdapter.submitList(expenses)

            budgetViewModel.triggerSync() // Trigger sync when MainActivity is created
        }

        // Navigation button listeners
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnNavigateAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.btnNavigateCategories.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        binding.btnNavigateReports.setOnClickListener {
            // TODO: Navigate to Reports Activity
            Toast.makeText(this, "Reports feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        //  (Add listener for the new button)
        binding.btnNavigateGoals.setOnClickListener {
            startActivity(Intent(this, GoalSettingActivity::class.java))
        }
        //
        binding.btnNavigateReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }


    }

    private fun showPhotoDialog(photoUriString: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_photo_view, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.ivFullPhoto)

        // Load the photo into the ImageView in the dialog
        Glide.with(this)
            .load(Uri.parse(photoUriString))
            .apply(RequestOptions().fitCenter()) // Or other options
            .into(imageView)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
