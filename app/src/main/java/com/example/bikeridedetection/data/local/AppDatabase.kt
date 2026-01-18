package com.example.bikeridedetection.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bikeridedetection.data.local.dao.CallHistoryDao
import com.example.bikeridedetection.data.local.dao.EmergencyContactDao
import com.example.bikeridedetection.data.local.entity.CallHistoryEntity
import com.example.bikeridedetection.data.local.entity.EmergencyContactEntity

/**
 * Room database for the BikeRideDetection app.
 */
@Database(
    entities = [CallHistoryEntity::class, EmergencyContactEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Gets the CallHistoryDao for database operations.
     */
    abstract fun callHistoryDao(): CallHistoryDao

    /**
     * Gets the EmergencyContactDao for database operations.
     */
    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        const val DATABASE_NAME = "bike_ride_detection_db"

        /**
         * Migration from version 1 to 2: Adds emergency_contacts table.
         */
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS emergency_contacts (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            phoneNumber TEXT NOT NULL,
                            displayName TEXT NOT NULL,
                            contactUri TEXT,
                            createdAt INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        CREATE UNIQUE INDEX IF NOT EXISTS index_emergency_contacts_phoneNumber
                        ON emergency_contacts (phoneNumber)
                        """.trimIndent(),
                    )
                }
            }
    }
}
