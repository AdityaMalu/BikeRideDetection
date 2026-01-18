package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for removing an emergency contact.
 */
class RemoveEmergencyContactUseCase
    @Inject
    constructor(
        private val repository: EmergencyContactRepository,
    ) {
        /**
         * Removes an emergency contact.
         *
         * @param contact The contact to remove
         */
        suspend operator fun invoke(contact: EmergencyContact) {
            Timber.d("Removing emergency contact: ${contact.displayName}")
            repository.removeContact(contact)
        }

        /**
         * Removes an emergency contact by ID.
         *
         * @param id The ID of the contact to remove
         */
        suspend fun byId(id: Long) {
            Timber.d("Removing emergency contact by ID: $id")
            repository.removeContactById(id)
        }
    }
