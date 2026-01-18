package com.example.bikeridedetection.di

import android.content.Context
import androidx.room.Room
import com.example.bikeridedetection.data.local.AppDatabase
import com.example.bikeridedetection.data.local.dao.CallHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).build()

    /**
     * Provides the CallHistoryDao instance.
     */
    @Provides
    @Singleton
    fun provideCallHistoryDao(database: AppDatabase): CallHistoryDao = database.callHistoryDao()
}

