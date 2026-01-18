package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import javax.inject.Inject

/**
 * Use case for saving a call history entry when a call is rejected.
 */
class SaveCallHistoryUseCase
    @Inject
    constructor(
        private val repository: CallHistoryRepository,
    ) {
    /**
     * Saves a new call history entry.
     *
     * @param phoneNumber The phone number of the caller
     * @param isFromContact Whether the caller is in contacts
     * @param autoReplyMessage The auto-reply message that was sent
     * @return The ID of the saved entry
     */
    suspend operator fun invoke(
        phoneNumber: String,
        isFromContact: Boolean,
        autoReplyMessage: String,
    ): Long {
        val entry =
            CallHistoryEntry(
                phoneNumber = phoneNumber,
                timestamp = System.currentTimeMillis(),
                isFromContact = isFromContact,
                autoReplyMessage = autoReplyMessage,
            )
        return repository.saveEntry(entry)
    }
}

