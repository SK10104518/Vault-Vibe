package com.example.vv.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.example.vv.data.db.entities.Expense
import com.example.vv.data.repository.ExpenseRepository

class ExpenseViewModel : ViewModel() {

    private val repo = ExpenseRepository()

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    fun loadExpenses(from: Timestamp, to: Timestamp) {
        repo.getExpensesForUserPeriod(from, to, {
            _expenses.value = it
        }, {
            // Log or notify error
        })
    }

    fun addExpense(expense: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.addExpense(expense, onSuccess, onFailure)
    }

    fun deleteExpense(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.deleteExpense(id, onSuccess, onFailure)
    }

    fun updateExpense(id: String, fields: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.updateExpense(id, fields, onSuccess, onFailure)
    }
}
