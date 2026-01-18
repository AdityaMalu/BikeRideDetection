package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for checking if a phone number is an emergency contact.
 */
class IsEmergencyContactUseCase
    @Inject
    constructor(
        private val repository: EmergencyContactRepository,
        private val bikeModeDataStore: BikeModeDataStore,
    ) {
        /**
         * Checks if a phone number is an emergency contact and should bypass call blocking.
         *
         * @param phoneNumber The phone number to check
         * @return true if the number is an emergency contact and the feature is enabled
         */
        suspend operator fun invoke(phoneNumber: String): Boolean {
            // Check if emergency contacts feature is enabled
            if (!bikeModeDataStore.isEmergencyContactsEnabled()) {
                Timber.d("Emergency contacts feature is disabled")
                return false
            }

            val isEmergency = repository.isEmergencyContact(phoneNumber)
            Timber.d("Is $phoneNumber an emergency contact: $isEmergency")
            return isEmergency
        }
    }
