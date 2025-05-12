package com.example.vv.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vv.data.db.entities.Category
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesCollection = firestore.collection("categories")

    fun addCategory(category: Category) {
        categoriesCollection.add(category)
            .addOnSuccessListener { documentReference ->
                // Category added successfully
            }
            .addOnFailureListener { e ->
                // Handle errors
            }
    }

    // Add functions to fetch categories
}