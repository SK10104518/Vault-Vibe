
package com.example.vv.data.db.daos

import androidx.room.*
import com.example.vv.data.db.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC, startTime DESC")
    fun getAllExpensesForUser(userId: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, startTime DESC")
    fun getExpensesForUserInDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Expense>>

    // New query to get total spending by category within a date range for a user
    @Query("SELECT categoryId, SUM(amount) AS totalAmount FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    fun getTotalSpendingByCategory(userId: String, startDate: Long, endDate: Long): Flow<List<CategorySpending>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): Expense?

    @Delete
    suspend fun deleteExpense(expense: Expense)
}
