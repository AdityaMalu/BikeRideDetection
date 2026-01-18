package com.example.bikeridedetection.di

import com.example.bikeridedetection.data.repository.BikeModeRepositoryImpl
import com.example.bikeridedetection.data.repository.CallHistoryRepositoryImpl
import com.example.bikeridedetection.data.repository.EmergencyContactRepositoryImpl
import com.example.bikeridedetection.data.repository.SmsRepositoryImpl
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import com.example.bikeridedetection.domain.repository.SmsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /**
     * Binds [BikeModeRepositoryImpl] to [BikeModeRepository].
     */
    @Binds
    @Singleton
    abstract fun bindBikeModeRepository(impl: BikeModeRepositoryImpl): BikeModeRepository

    /**
     * Binds [SmsRepositoryImpl] to [SmsRepository].
     */
    @Binds
    @Singleton
    abstract fun bindSmsRepository(impl: SmsRepositoryImpl): SmsRepository

    /**
     * Binds [CallHistoryRepositoryImpl] to [CallHistoryRepository].
     */
    @Binds
    @Singleton
    abstract fun bindCallHistoryRepository(impl: CallHistoryRepositoryImpl): CallHistoryRepository

    /**
     * Binds [EmergencyContactRepositoryImpl] to [EmergencyContactRepository].
     */
    @Binds
    @Singleton
    abstract fun bindEmergencyContactRepository(impl: EmergencyContactRepositoryImpl): EmergencyContactRepository
}
