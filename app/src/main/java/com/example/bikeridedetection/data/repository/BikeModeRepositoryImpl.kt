package com.example.bikeridedetection.data.repository

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [BikeModeRepository] using DataStore.
 */
@Singleton
class BikeModeRepositoryImpl @Inject constructor(
    private val dataStore: BikeModeDataStore
) : BikeModeRepository {

    override fun observeBikeMode(): Flow<BikeMode> = dataStore.observeBikeMode()

    override suspend fun getBikeMode(): BikeMode = dataStore.getBikeMode()

    override suspend fun setBikeModeEnabled(enabled: Boolean) {
        dataStore.setBikeModeEnabled(enabled)
    }

    override suspend fun setAutoReplyMessage(message: String) {
        dataStore.setAutoReplyMessage(message)
    }
}

