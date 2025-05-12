package com.example.vv.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.vv.data.db.entities.Category
import com.example.vv.databinding.ActivityCategoryBinding
import com.example.vv.ui.viewmodel.BudgetViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.vv.VV
import com.example.vv.ui.viewmodel.BudgetViewModelFactory

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory((application as VV).repository)
    }
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveCategory.setOnClickListener {
            saveCategory()
        }
    }

    private fun saveCategory() {
        val categoryName = binding.etCategoryName.text.toString().trim()

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val category = Category(name = categoryName, userId = userId)
        budgetViewModel.insertCategory(category)

        Toast.makeText(this, "Category saved!", Toast.LENGTH_SHORT).show()
        finish() // Go back to the previous activity
    }
}
