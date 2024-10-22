package com.example.android.advancedcoroutines.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.advancedcoroutines.domain.model.Plant

/**
 * The Room database for this app
 */

@Database(entities = [Plant::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}