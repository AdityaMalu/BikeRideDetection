package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for adding an emergency contact.
 */
class AddEmergencyContactUseCase
    @Inject
    constructor(
        private val repository: EmergencyContactRepository,
    ) {
        /**
         * Adds an emergency contact.
         *
         * @param contact The contact to add
         * @return The ID of the added contact
         */
        suspend operator fun invoke(contact: EmergencyContact): Long {
            Timber.d("Adding emergency contact: ${contact.displayName}")
            return repository.addContact(contact)
        }

        /**
         * Adds an emergency contact with the given details.
         *
         * @param phoneNumber The phone number
         * @param displayName The display name
         * @param contactUri Optional URI to the system contact
         * @return The ID of the added contact
         */
        suspend operator fun invoke(
            phoneNumber: String,
            displayName: String,
            contactUri: String? = null,
        ): Long {
            val contact =
                EmergencyContact(
                    phoneNumber = phoneNumber,
                    displayName = displayName,
                    contactUri = contactUri,
                )
            return invoke(contact)
        }
    }
