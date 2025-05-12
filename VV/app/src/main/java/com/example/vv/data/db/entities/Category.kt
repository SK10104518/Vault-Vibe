package com.example.vv.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val userId: String,
    val synced: Boolean = false // Flag to indicate if synced with Firestore
)
