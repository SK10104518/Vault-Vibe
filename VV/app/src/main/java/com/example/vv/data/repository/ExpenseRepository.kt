package com.example.vv.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.vv.data.db.entities.Expense
import com.google.firebase.Timestamp

class ExpenseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val expenseRef = db.collection("expenses")

            .addOnSuccessListener { onSuccess() }
        fun addExpense(expense: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
            expenseRef.add(expense)
            .addOnFailureListener { onFailure(it) }
    }

    fun getExpensesForUserPeriod(
        from: Timestamp,
        to: Timestamp,
        onResult: (List<Expense>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        expenseRef
            .whereGreaterThanOrEqualTo("timestamp", from)
            .whereLessThanOrEqualTo("timestamp", to)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val expenses = snapshot.documents.mapNotNull { it.toObject(Expense::class.java)?.copy(id = it.id) }
                onResult(expenses)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteExpense(expenseId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        expenseRef.document(expenseId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateExpense(expenseId: String, updated: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        expenseRef.document(expenseId).update(updated)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
