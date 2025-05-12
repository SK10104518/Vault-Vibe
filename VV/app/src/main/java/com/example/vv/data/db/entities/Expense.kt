package com.example.vv.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // To associate expenses with a user
    val description: String,
    val date: Long, // Store date as timestamp
    val startTime: String,
    val endTime: String,
    val categoryId: Int, // Foreign key to Category
    val amount: Double, // Added for budget tracking
    val photoUri: String? // Store the URI of the local photo
)
