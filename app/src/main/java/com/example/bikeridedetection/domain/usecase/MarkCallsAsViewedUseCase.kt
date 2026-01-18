package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import javax.inject.Inject

/**
 * Use case for marking all unviewed call history entries as viewed.
 */
class MarkCallsAsViewedUseCase
    @Inject
    constructor(
        private val repository: CallHistoryRepository,
    ) {
        /**
         * Marks all unviewed entries as viewed with the current timestamp.
         */
        suspend operator fun invoke() {
            repository.markAllAsViewed()
        }
    }
