package com.example.bikeridedetection.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikeridedetection.data.local.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for emergency contact operations.
 */
@Dao
interface EmergencyContactDao {
    /**
     * Inserts a new emergency contact.
     *
     * @param contact The contact to insert
     * @return The ID of the inserted contact
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: EmergencyContactEntity): Long

    /**
     * Deletes an emergency contact.
     *
     * @param contact The contact to delete
     */
    @Delete
    suspend fun delete(contact: EmergencyContactEntity)

    /**
     * Deletes an emergency contact by ID.
     *
     * @param id The ID of the contact to delete
     */
    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Gets all emergency contacts ordered by display name.
     *
     * @return A Flow of all emergency contacts
     */
    @Query("SELECT * FROM emergency_contacts ORDER BY displayName ASC")
    fun getAllContacts(): Flow<List<EmergencyContactEntity>>

    /**
     * Gets all emergency contacts as a list (non-Flow).
     *
     * @return List of all emergency contacts
     */
    @Query("SELECT * FROM emergency_contacts")
    suspend fun getAllContactsList(): List<EmergencyContactEntity>

    /**
     * Gets the count of emergency contacts.
     *
     * @return A Flow of the contact count
     */
    @Query("SELECT COUNT(*) FROM emergency_contacts")
    fun getContactCount(): Flow<Int>

    /**
     * Checks if a phone number is in the emergency contacts list.
     * Uses LIKE for flexible matching (handles different phone number formats).
     *
     * @param phoneNumber The phone number to check
     * @return true if the number is an emergency contact
     */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM emergency_contacts 
            WHERE phoneNumber = :phoneNumber 
            OR phoneNumber LIKE '%' || :phoneNumber 
            OR :phoneNumber LIKE '%' || phoneNumber
        )
        """,
    )
    suspend fun isEmergencyContact(phoneNumber: String): Boolean

    /**
     * Gets a single contact by ID.
     *
     * @param id The contact ID
     * @return The contact or null if not found
     */
    @Query("SELECT * FROM emergency_contacts WHERE id = :id")
    suspend fun getContactById(id: Long): EmergencyContactEntity?

    /**
     * Gets a contact by phone number.
     *
     * @param phoneNumber The phone number to search for
     * @return The contact or null if not found
     */
    @Query("SELECT * FROM emergency_contacts WHERE phoneNumber = :phoneNumber")
    suspend fun getContactByPhoneNumber(phoneNumber: String): EmergencyContactEntity?

    /**
     * Deletes all emergency contacts.
     */
    @Query("DELETE FROM emergency_contacts")
    suspend fun deleteAll()
}
