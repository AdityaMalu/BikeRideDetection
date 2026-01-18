package com.example.bikeridedetection.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bikeridedetection.data.local.dao.CallHistoryDao
import com.example.bikeridedetection.data.local.entity.CallHistoryEntity

/**
 * Room database for the BikeRideDetection app.
 */
@Database(
    entities = [CallHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Gets the CallHistoryDao for database operations.
     */
    abstract fun callHistoryDao(): CallHistoryDao

    companion object {
        const val DATABASE_NAME = "bike_ride_detection_db"
    }
}

