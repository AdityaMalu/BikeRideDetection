package com.example.bikeridedetection.domain.repository

import com.example.bikeridedetection.domain.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing emergency contacts.
 */
interface EmergencyContactRepository {
    /**
     * Adds a new emergency contact.
     *
     * @param contact The contact to add
     * @return The ID of the added contact
     */
    suspend fun addContact(contact: EmergencyContact): Long

    /**
     * Removes an emergency contact.
     *
     * @param contact The contact to remove
     */
    suspend fun removeContact(contact: EmergencyContact)

    /**
     * Removes an emergency contact by ID.
     *
     * @param id The ID of the contact to remove
     */
    suspend fun removeContactById(id: Long)

    /**
     * Gets all emergency contacts.
     *
     * @return A Flow of all emergency contacts
     */
    fun getAllContacts(): Flow<List<EmergencyContact>>

    /**
     * Gets the count of emergency contacts.
     *
     * @return A Flow of the contact count
     */
    fun getContactCount(): Flow<Int>

    /**
     * Checks if a phone number is an emergency contact.
     *
     * @param phoneNumber The phone number to check
     * @return true if the number is an emergency contact
     */
    suspend fun isEmergencyContact(phoneNumber: String): Boolean

    /**
     * Gets a contact by phone number.
     *
     * @param phoneNumber The phone number to search for
     * @return The contact or null if not found
     */
    suspend fun getContactByPhoneNumber(phoneNumber: String): EmergencyContact?

    /**
     * Deletes all emergency contacts.
     */
    suspend fun deleteAll()
}
