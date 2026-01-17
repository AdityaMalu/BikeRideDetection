package com.example.bikeridedetection.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bikeridedetection.domain.model.BikeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "bike_mode_preferences"
)

/**
 * DataStore-based data source for bike mode preferences.
 */
@Singleton
class BikeModeDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val BIKE_MODE_ENABLED = booleanPreferencesKey("bike_mode_enabled")
        val AUTO_REPLY_MESSAGE = stringPreferencesKey("auto_reply_message")
    }

    /**
     * Observes the current bike mode state.
     */
    fun observeBikeMode(): Flow<BikeMode> = context.dataStore.data.map { preferences ->
        BikeMode(
            isEnabled = preferences[PreferencesKeys.BIKE_MODE_ENABLED] ?: false,
            autoReplyMessage = preferences[PreferencesKeys.AUTO_REPLY_MESSAGE]
                ?: BikeMode.DEFAULT_AUTO_REPLY
        )
    }

    /**
     * Gets the current bike mode state.
     */
    suspend fun getBikeMode(): BikeMode {
        val preferences = context.dataStore.data.first()
        return BikeMode(
            isEnabled = preferences[PreferencesKeys.BIKE_MODE_ENABLED] ?: false,
            autoReplyMessage = preferences[PreferencesKeys.AUTO_REPLY_MESSAGE]
                ?: BikeMode.DEFAULT_AUTO_REPLY
        )
    }

    /**
     * Sets whether bike mode is enabled.
     */
    suspend fun setBikeModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIKE_MODE_ENABLED] = enabled
        }
    }

    /**
     * Sets the auto-reply message.
     */
    suspend fun setAutoReplyMessage(message: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REPLY_MESSAGE] = message
        }
    }
}

