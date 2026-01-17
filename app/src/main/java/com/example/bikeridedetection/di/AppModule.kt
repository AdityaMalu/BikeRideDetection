package com.example.bikeridedetection.di

import android.content.Context
import android.telephony.SmsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the SmsManager instance.
     */
    @Provides
    @Singleton
    fun provideSmsManager(
        @ApplicationContext context: Context,
    ): SmsManager = context.getSystemService(SmsManager::class.java)
}
