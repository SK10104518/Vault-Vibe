package com.example.vv.data

import android.content.Context
import androidx.room.Room
import com.example.vv.data.db.AppDatabase

object AppDatabaseInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "vault_vibe_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }

}