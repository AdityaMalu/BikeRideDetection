package com.example.bikeridedetection.domain.model

/**
 * Represents a call history entry for a rejected call during bike mode.
 *
 * @property id Unique identifier for the entry
 * @property phoneNumber The phone number of the caller
 * @property timestamp The timestamp when the call was received (in milliseconds)
 * @property isFromContact Whether the caller is in the user's contacts
 * @property autoReplyMessage The auto-reply message that was sent to the caller
 * @property isViewed Whether the user has viewed this entry
 * @property viewedAt The timestamp when the entry was marked as viewed (null if not viewed)
 */
data class CallHistoryEntry(
    val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val isFromContact: Boolean = false,
    val autoReplyMessage: String,
    val isViewed: Boolean = false,
    val viewedAt: Long? = null,
)

