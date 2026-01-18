package com.example.bikeridedetection.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikeridedetection.data.local.entity.CallHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for call history operations.
 */
@Dao
interface CallHistoryDao {
    /**
     * Inserts a new call history entry.
     *
     * @param entry The entry to insert
     * @return The ID of the inserted entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CallHistoryEntity): Long

    /**
     * Gets all call history entries ordered by timestamp descending.
     *
     * @return A Flow of all call history entries
     */
    @Query("SELECT * FROM call_history ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<CallHistoryEntity>>

    /**
     * Gets all unviewed call history entries.
     *
     * @return A Flow of unviewed entries
     */
    @Query("SELECT * FROM call_history WHERE isViewed = 0 ORDER BY timestamp DESC")
    fun getUnviewedEntries(): Flow<List<CallHistoryEntity>>

    /**
     * Gets the count of unviewed entries.
     *
     * @return A Flow of the unviewed count
     */
    @Query("SELECT COUNT(*) FROM call_history WHERE isViewed = 0")
    fun getUnviewedCount(): Flow<Int>

    /**
     * Marks all unviewed entries as viewed with the current timestamp.
     *
     * @param viewedAt The timestamp when entries were viewed
     */
    @Query("UPDATE call_history SET isViewed = 1, viewedAt = :viewedAt WHERE isViewed = 0")
    suspend fun markAllAsViewed(viewedAt: Long)

    /**
     * Deletes entries that have been viewed and are older than the specified threshold.
     *
     * @param thresholdTime Entries viewed before this time will be deleted
     * @return The number of deleted entries
     */
    @Query("DELETE FROM call_history WHERE isViewed = 1 AND viewedAt IS NOT NULL AND viewedAt < :thresholdTime")
    suspend fun deleteOldViewedEntries(thresholdTime: Long): Int

    /**
     * Deletes all call history entries.
     */
    @Query("DELETE FROM call_history")
    suspend fun deleteAll()

    /**
     * Gets a single entry by ID.
     *
     * @param id The entry ID
     * @return The entry or null if not found
     */
    @Query("SELECT * FROM call_history WHERE id = :id")
    suspend fun getEntryById(id: Long): CallHistoryEntity?
}
