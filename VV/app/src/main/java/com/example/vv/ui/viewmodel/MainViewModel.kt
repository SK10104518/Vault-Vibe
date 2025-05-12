package com.example.vv.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vv.VV
import com.example.vv.data.SyncManager
import com.example.vv.data.db.entities.Category
import com.example.vv.data.db.entities.Expense
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VV.database
    private val firestore = FirebaseFirestore.getInstance()
    private val syncManager = SyncManager(db, firestore)

    fun startSync(userId: String) {
        syncManager.startExpenseSync(userId)
        syncManager.startCategorySync(userId)
    }

    fun stopSync() {
        syncManager.stopExpenseSync()
        syncManager.stopCategorySync()
    }

    fun saveExpense(expense: Expense, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = db.expenseEntryDao().insert(expense)
            // After local insert, upload to Firestore
            syncManager.uploadExpenseEntry(expense.copy(id = id.toInt()), userId)
        }
        fun saveCategory(category: Category, userId: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val id = db.categoryDao().insert(category)
                syncManager.uploadCategory(category.copy(id = id.toInt()), userId)
            }
        }
    }
}
