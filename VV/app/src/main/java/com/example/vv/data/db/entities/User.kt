package com.example.vv.data.db.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String, // Firebase User ID
    val email: String,
    val password: String,
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 0.0
)
