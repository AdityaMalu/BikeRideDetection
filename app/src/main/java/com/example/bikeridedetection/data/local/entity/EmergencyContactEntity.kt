package com.example.bikeridedetection.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bikeridedetection.domain.model.EmergencyContact

/**
 * Room entity representing an emergency contact in the database.
 * Emergency contacts can bypass call blocking during bike mode.
 */
@Entity(
    tableName = "emergency_contacts",
    indices = [Index(value = ["phoneNumber"], unique = true)],
)
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val displayName: String,
    val contactUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
) {
    /**
     * Converts this entity to a domain model.
     */
    fun toDomainModel(): EmergencyContact =
        EmergencyContact(
            id = id,
            phoneNumber = phoneNumber,
            displayName = displayName,
            contactUri = contactUri,
            createdAt = createdAt,
        )

    companion object {
        /**
         * Creates an entity from a domain model.
         */
        fun fromDomainModel(contact: EmergencyContact): EmergencyContactEntity =
            EmergencyContactEntity(
                id = contact.id,
                phoneNumber = contact.phoneNumber,
                displayName = contact.displayName,
                contactUri = contact.contactUri,
                createdAt = contact.createdAt,
            )
    }
}
