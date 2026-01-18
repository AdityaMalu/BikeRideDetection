package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all emergency contacts.
 */
class GetEmergencyContactsUseCase
    @Inject
    constructor(
        private val repository: EmergencyContactRepository,
    ) {
        /**
         * Gets all emergency contacts.
         *
         * @return A Flow of all emergency contacts
         */
        operator fun invoke(): Flow<List<EmergencyContact>> = repository.getAllContacts()
    }
