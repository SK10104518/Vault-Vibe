// CategoryDao.kt

package com.example.vv.data.db.daos

import androidx.room.*
import com.example.vv.data.db.entities.Category
import kotlinx.coroutines.flow.Flow // Import Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category): Long

    // Function to get all categories for a specific user, returns a Flow
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    fun getAllCategoriesForUser(userId: String): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category?

    @Delete
    suspend fun deleteCategory(category: Category)

    // CategoryDao.kt (Add query for unsynced categories)
    @Query("SELECT * FROM categories WHERE synced = 0 AND userId = :userId")
    suspend fun getUnsyncedCategoriesForUser(userId: String): List<Category>

    @Update // Add update method to mark categories as synced
    suspend fun updateCategory(category: Category)

}
