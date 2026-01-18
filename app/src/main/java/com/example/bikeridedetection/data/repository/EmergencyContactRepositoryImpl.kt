package com.example.bikeridedetection.data.repository

import com.example.bikeridedetection.data.local.dao.EmergencyContactDao
import com.example.bikeridedetection.data.local.entity.EmergencyContactEntity
import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [EmergencyContactRepository] using Room database.
 */
@Singleton
class EmergencyContactRepositoryImpl
    @Inject
    constructor(
        private val emergencyContactDao: EmergencyContactDao,
    ) : EmergencyContactRepository {
        override suspend fun addContact(contact: EmergencyContact): Long {
            Timber.d("Adding emergency contact: ${contact.displayName}")
            val entity = EmergencyContactEntity.fromDomainModel(contact)
            val id = emergencyContactDao.insert(entity)
            Timber.d("Emergency contact added with ID: $id")
            return id
        }

        override suspend fun removeContact(contact: EmergencyContact) {
            Timber.d("Removing emergency contact: ${contact.displayName} with ID: ${contact.id}")
            if (contact.id > 0) {
                // Use deleteById for reliable deletion by primary key
                emergencyContactDao.deleteById(contact.id)
                Timber.d("Emergency contact deleted by ID: ${contact.id}")
            } else {
                // Fallback to entity-based deletion if ID is not set
                Timber.w("Contact ID is 0, attempting entity-based deletion")
                val entity = EmergencyContactEntity.fromDomainModel(contact)
                emergencyContactDao.delete(entity)
            }
        }

        override suspend fun removeContactById(id: Long) {
            Timber.d("Removing emergency contact by ID: $id")
            emergencyContactDao.deleteById(id)
        }

        override fun getAllContacts(): Flow<List<EmergencyContact>> =
            emergencyContactDao.getAllContacts().map { entities ->
                entities.map { it.toDomainModel() }
            }

        override fun getContactCount(): Flow<Int> = emergencyContactDao.getContactCount()

        override suspend fun isEmergencyContact(phoneNumber: String): Boolean {
            if (phoneNumber.isBlank()) {
                return false
            }
            val normalizedNumber = normalizePhoneNumber(phoneNumber)
            val isEmergency = emergencyContactDao.isEmergencyContact(normalizedNumber)
            Timber.d("Checking if $phoneNumber is emergency contact: $isEmergency")
            return isEmergency
        }

        override suspend fun getContactByPhoneNumber(phoneNumber: String): EmergencyContact? {
            val normalizedNumber = normalizePhoneNumber(phoneNumber)
            return emergencyContactDao.getContactByPhoneNumber(normalizedNumber)?.toDomainModel()
        }

        override suspend fun deleteAll() {
            Timber.d("Deleting all emergency contacts")
            emergencyContactDao.deleteAll()
        }

        /**
         * Normalizes a phone number by removing common formatting characters.
         */
        private fun normalizePhoneNumber(phoneNumber: String): String = phoneNumber.replace(Regex("[\\s\\-().]"), "")
    }
