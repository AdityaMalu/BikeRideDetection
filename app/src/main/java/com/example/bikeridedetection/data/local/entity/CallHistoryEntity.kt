package com.example.bikeridedetection.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bikeridedetection.domain.model.CallHistoryEntry

/**
 * Room entity representing a call history entry in the database.
 */
@Entity(tableName = "call_history")
data class CallHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val isFromContact: Boolean,
    val autoReplyMessage: String,
    val isViewed: Boolean = false,
    val viewedAt: Long? = null,
) {
    /**
     * Converts this entity to a domain model.
     */
    fun toDomainModel(): CallHistoryEntry =
        CallHistoryEntry(
            id = id,
            phoneNumber = phoneNumber,
            timestamp = timestamp,
            isFromContact = isFromContact,
            autoReplyMessage = autoReplyMessage,
            isViewed = isViewed,
            viewedAt = viewedAt,
        )

    companion object {
        /**
         * Creates an entity from a domain model.
         */
        fun fromDomainModel(entry: CallHistoryEntry): CallHistoryEntity =
            CallHistoryEntity(
                id = entry.id,
                phoneNumber = entry.phoneNumber,
                timestamp = entry.timestamp,
                isFromContact = entry.isFromContact,
                autoReplyMessage = entry.autoReplyMessage,
                isViewed = entry.isViewed,
                viewedAt = entry.viewedAt,
            )
    }
}

