package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting call history entries.
 */
class GetCallHistoryUseCase
    @Inject
    constructor(
        private val repository: CallHistoryRepository,
    ) {
    /**
     * Gets all call history entries ordered by timestamp descending.
     *
     * @return A Flow of all call history entries
     */
    operator fun invoke(): Flow<List<CallHistoryEntry>> = repository.getAllEntries()

    /**
     * Gets the count of unviewed entries.
     *
     * @return A Flow of the unviewed count
     */
    fun getUnviewedCount(): Flow<Int> = repository.getUnviewedCount()
}

