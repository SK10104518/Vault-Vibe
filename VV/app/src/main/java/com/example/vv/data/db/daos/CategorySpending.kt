package com.example.vv.data.db.daos

// Data class to hold the result of the category spending query
// Room will automatically map the columns from the query to these properties
data class CategorySpending(
    val categoryId: Int,
    val totalAmount: Double
)
