package com.example.bikeridedetection.domain.model

/**
 * Represents an emergency contact that can bypass call blocking during bike mode.
 *
 * @property id Unique identifier for the contact
 * @property phoneNumber The phone number of the contact
 * @property displayName The display name of the contact
 * @property contactUri Optional URI to the system contact
 * @property createdAt Timestamp when the contact was added
 */
data class EmergencyContact(
    val id: Long = 0,
    val phoneNumber: String,
    val displayName: String,
    val contactUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
