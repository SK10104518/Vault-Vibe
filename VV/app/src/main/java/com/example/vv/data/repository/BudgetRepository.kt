// BudgetRepository.kt

package com.example.vv.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.vv.data.db.AppDatabase
import com.example.vv.data.db.daos.CategorySpending
import com.example.vv.data.db.entities.Category
import com.example.vv.data.db.entities.Expense
import com.example.vv.data.db.entities.User
import kotlinx.coroutines.flow.Flow // Import Flow
import kotlinx.coroutines.tasks.await

class BudgetRepository(private val database: AppDatabase) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // --- Room Database Operations ---

    suspend fun insertCategory(category: Category) = database.categoryDao().insertCategory(category)
    // Function to get all categories for a user, returns Flow from DAO
    fun getAllCategoriesForUser(userId: String): Flow<List<Category>> = database.categoryDao().getAllCategoriesForUser(userId)
    suspend fun getCategoryById(categoryId: Int) = database.categoryDao().getCategoryById(categoryId)
    suspend fun deleteCategory(category: Category) = database.categoryDao().deleteCategory(category)

    suspend fun insertExpense(expense: Expense) = database.expenseDao().insertExpense(expense)
    fun getAllExpensesForUser(userId: String): Flow<List<Expense>> = database.expenseDao().getAllExpensesForUser(userId)
    fun getExpensesForUserInDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Expense>> =
        database.expenseDao().getExpensesForUserInDateRange(userId, startDate, endDate)
    fun getTotalSpendingByCategory(userId: String, startDate: Long, endDate: Long): Flow<List<CategorySpending>> =
        database.expenseDao().getTotalSpendingByCategory(userId, startDate, endDate)
    suspend fun getExpenseById(expenseId: Int) = database.expenseDao().getExpenseById(expenseId)
    suspend fun deleteExpense(expense: Expense) = database.expenseDao().deleteExpense(expense)

    // User
    suspend fun insertUser(user: com.example.vv.data.db.entities.User) = database.userDao().insertUser(user)
    suspend fun getUserByUserId(userId: String) = database.userDao().getUserByUserId(userId)
    suspend fun updateMonthlyGoals(userId: String, minGoal: Double, maxGoal: Double) {
        val user = database.userDao().getUserByUserId(userId)
        if (user != null) {
            val updatedUser = user.copy(minMonthlyGoal = minGoal, maxMonthlyGoal = maxGoal)
            database.userDao().updateUser(updatedUser)
            // TODO: Implement sync with Firestore if needed
        }
    }
    suspend fun getUser(userId: String): Flow<com.example.vv.data.db.entities.User?> = database.userDao().getUserByUserId(userId)


    // --- Firebase Operations (Examples - you'll need to implement sync logic) ---

    // Example of saving a category to Firestore (requires sync logic to keep Room and Firestore consistent)
    suspend fun saveCategoryToFirestore(category: Category) {
        firestore.collection("categories").add(category).await()
    }

    // Example of fetching categories from Firestore (requires sync logic)
    suspend fun fetchCategoriesFromFirestore(userId: String): List<Category> {
        val snapshot = firestore.collection("categories").whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(Category::class.java)
    }

    // Example of uploading a photo to Firebase Storage
    suspend fun uploadPhotoToFirebaseStorage(uri: Uri): String {
        val ref = storage.reference.child("expense_photos/${uri.lastPathSegment}")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
    suspend fun
    synchronizeData() {
        try {
            val firestoreCategories = firestore.collection("categories").get().await()
            val localCategories = database.categoryDao().getAllCategories()
            val firestoreExpenses = firestore.collection("expenses").get().await()
            val localExpenses = database.expenseDao().getAllExpenses()

            }
        catch (e: Exception) {
            Log.e("BudgetRepository", "Error synchronizing data", e)

        }

    }

    // Function to sync unsynced categories to Firestore
    suspend fun syncCategoriesToFirestore(userId: String) {
        val unsyncedCategories = database.categoryDao().getUnsyncedCategoriesForUser(userId)
        for (category in unsyncedCategories) {
            try {
                // Add category to Firestore
                val documentReference = firestore.collection("categories").add(category).await()
                // If successful, mark the category as synced in Room
                val syncedCategory = category.copy(synced = true)
                database.categoryDao().updateCategory(syncedCategory)
                Log.d("BudgetRepository", "Synced category: ${category.name} to Firestore")
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to sync category: ${category.name}", e)
                // Handle the error (e.g., retry later, log to crashlytics)
            }
        }
    }

}
