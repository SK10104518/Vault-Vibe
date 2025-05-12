// BudgetViewModel.kt
package com.example.vv.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.* // Import necessary lifecycle components
import com.google.firebase.auth.FirebaseAuth
import com.example.vv.data.db.entities.Category
import com.example.vv.data.db.entities.Expense
import com.example.vv.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: throw IllegalStateException("User not logged in")

    // Categories
    // Expose the Flow of categories from the repository as LiveData
    val allCategories: LiveData<List<Category>> = repository.getAllCategoriesForUser(userId).asLiveData()

    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
        // TODO: Implement sync with Firestore if needed
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
        // TODO: Implement sync with Firestore if needed
    }

    // Expenses
    val allExpenses: LiveData<List<Expense>> = repository.getAllExpensesForUser(userId).asLiveData()

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
        // TODO: Implement sync with Firestore if needed
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
        // TODO: Implement sync with Firestore if needed
    }

    fun getExpensesInDateRange(startDate: Long, endDate: Long) =
        repository.getExpensesForUserInDateRange(userId, startDate, endDate).asLiveData()

    fun getTotalSpendingByCategory(startDate: Long, endDate: Long) =
        repository.getTotalSpendingByCategory(userId, startDate, endDate).asLiveData()

    // Goal Setting
    fun setMonthlyGoals(minGoal: Double, maxGoal: Double) = viewModelScope.launch {
        repository.updateMonthlyGoals(userId, minGoal, maxGoal)

        fun triggerSync() {
            viewModelScope.launch {
                repository.synchronizeData()
            }
        }


    }

    val currentUserData: LiveData<com.example.vv.data.db.entities.User?> = repository.getUser(userId).asLiveData()

    // Photo Upload (Handles photo upload before saving expense)
    fun uploadExpensePhoto(photoUri: Uri, expense: Expense, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val photoUrl = repository.uploadPhotoToFirebaseStorage(photoUri)
                val expenseWithPhoto = expense.copy(photoUri = photoUrl) // Update expense with photo URL
                insertExpense(expenseWithPhoto)
                onComplete(photoUrl)
            } catch (e: Exception) {
                Log.e("BudgetViewModel", "Photo upload failed", e)
                onComplete(null)
            }
        }
    }
}

// ViewModel Factory to inject Repository
class BudgetViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
