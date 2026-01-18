package com.example.bikeridedetection.data.repository

import com.example.bikeridedetection.data.local.dao.CallHistoryDao
import com.example.bikeridedetection.data.local.entity.CallHistoryEntity
import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CallHistoryRepository] using Room database.
 */
@Singleton
class CallHistoryRepositoryImpl
    @Inject
    constructor(
        private val callHistoryDao: CallHistoryDao,
    ) : CallHistoryRepository {
        override suspend fun saveEntry(entry: CallHistoryEntry): Long {
            Timber.d("CallHistoryRepositoryImpl.saveEntry: $entry")
            val entity = CallHistoryEntity.fromDomainModel(entry)
            Timber.d("Inserting entity: $entity")
            val id = callHistoryDao.insert(entity)
            Timber.d("Inserted with ID: $id")
            return id
        }

        override fun getAllEntries(): Flow<List<CallHistoryEntry>> =
            callHistoryDao.getAllEntries().map { entities ->
                entities.map { it.toDomainModel() }
            }

        override fun getUnviewedEntries(): Flow<List<CallHistoryEntry>> =
            callHistoryDao.getUnviewedEntries().map { entities ->
                entities.map { it.toDomainModel() }
            }

        override fun getUnviewedCount(): Flow<Int> = callHistoryDao.getUnviewedCount()

        override suspend fun markAllAsViewed() {
            callHistoryDao.markAllAsViewed(System.currentTimeMillis())
        }

        override suspend fun deleteOldViewedEntries(retentionPeriodMillis: Long): Int {
            val thresholdTime = System.currentTimeMillis() - retentionPeriodMillis
            return callHistoryDao.deleteOldViewedEntries(thresholdTime)
        }

        override suspend fun deleteAll() {
            callHistoryDao.deleteAll()
        }
    }
