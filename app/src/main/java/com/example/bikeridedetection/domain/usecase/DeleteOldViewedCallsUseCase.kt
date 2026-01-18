package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import javax.inject.Inject

/**
 * Use case for deleting old viewed call history entries.
 * Entries are deleted 48 hours after being marked as viewed.
 */
class DeleteOldViewedCallsUseCase
    @Inject
    constructor(
        private val repository: CallHistoryRepository,
    ) {
        /**
         * Deletes entries that have been viewed for more than 48 hours.
         *
         * @return The number of deleted entries
         */
        suspend operator fun invoke(): Int = repository.deleteOldViewedEntries()
    }
