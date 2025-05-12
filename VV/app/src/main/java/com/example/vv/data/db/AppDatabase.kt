package com.example.vv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vv.data.db.daos.CategoryDao
import com.example.vv.data.db.entities.Category
import com.example.vv.data.db.entities.Expense
import com.example.vv.data.db.daos.ExpenseDao
import com.example.vv.data.db.entities.User
import com.example.vv.data.db.daos.UserDao

@Database(
    entities = [User::class, Category::class, Expense::class],
    version = 1,
    exportSchema = false // Set to true to export schema JSON
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


