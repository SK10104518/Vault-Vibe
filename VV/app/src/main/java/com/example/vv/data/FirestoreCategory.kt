package com.example.vv.data

data class FirestoreCategory(
    val id: String = "", // Firestore document ID
    val name: String = "",
    val lastModified: Long = System.currentTimeMillis()
)
