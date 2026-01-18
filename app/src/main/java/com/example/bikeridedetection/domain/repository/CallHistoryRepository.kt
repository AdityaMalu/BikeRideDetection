package com.example.bikeridedetection.domain.repository

import com.example.bikeridedetection.domain.model.CallHistoryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing call history entries.
 */
interface CallHistoryRepository {
    /**
     * Saves a new call history entry.
     *
     * @param entry The entry to save
     * @return The ID of the saved entry
     */
    suspend fun saveEntry(entry: CallHistoryEntry): Long

    /**
     * Gets all call history entries ordered by timestamp descending.
     *
     * @return A Flow of all call history entries
     */
    fun getAllEntries(): Flow<List<CallHistoryEntry>>

    /**
     * Gets all unviewed call history entries.
     *
     * @return A Flow of unviewed entries
     */
    fun getUnviewedEntries(): Flow<List<CallHistoryEntry>>

    /**
     * Gets the count of unviewed entries.
     *
     * @return A Flow of the unviewed count
     */
    fun getUnviewedCount(): Flow<Int>

    /**
     * Marks all unviewed entries as viewed.
     */
    suspend fun markAllAsViewed()

    /**
     * Deletes entries that have been viewed for more than the specified duration.
     *
     * @param retentionPeriodMillis The retention period in milliseconds (default: 48 hours)
     * @return The number of deleted entries
     */
    suspend fun deleteOldViewedEntries(retentionPeriodMillis: Long = RETENTION_PERIOD_MILLIS): Int

    /**
     * Deletes all call history entries.
     */
    suspend fun deleteAll()

    companion object {
        /**
         * Default retention period: 24 hours in milliseconds.
         */
        const val RETENTION_PERIOD_MILLIS = 24 * 60 * 60 * 1000L
    }
}
