package com.example.vv

import android.app.Application
import com.example.vv.data.db.AppDatabase
import com.example.vv.data.repository.BudgetRepository

class VV: Application() {
    // Using lazy so the database and the repository are only created when they're needed
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BudgetRepository(database) }
}