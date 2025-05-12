package com.example.vv.data

data class FirestoreExpenseEntry(
    val id: String = "", // Firestore document ID
    val date: Long = 0L,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val description: String = "",
    val categoryId: Int = 0,
    val amount: Double = 0.0,
    val photoUri: String? = null,
    val lastModified: Long = System.currentTimeMillis() // for conflict resolution
)
