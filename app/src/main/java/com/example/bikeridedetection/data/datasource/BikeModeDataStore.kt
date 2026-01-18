package com.example.bikeridedetection.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.model.RepeatedCallerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "bike_mode_preferences",
)

/**
 * DataStore-based data source for bike mode preferences.
 */
@Singleton
class BikeModeDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private object PreferencesKeys {
            val BIKE_MODE_ENABLED = booleanPreferencesKey("bike_mode_enabled")
            val AUTO_REPLY_MESSAGE = stringPreferencesKey("auto_reply_message")
            val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

            // Repeated caller detection settings
            val REPEATED_CALLER_ENABLED = booleanPreferencesKey("repeated_caller_enabled")
            val REPEATED_CALLER_THRESHOLD = intPreferencesKey("repeated_caller_threshold")
            val REPEATED_CALLER_TIME_WINDOW = intPreferencesKey("repeated_caller_time_window")

            // Emergency contacts settings
            val EMERGENCY_CONTACTS_ENABLED = booleanPreferencesKey("emergency_contacts_enabled")

            // Allow next call (temporary bypass)
            val ALLOW_NEXT_CALL = booleanPreferencesKey("allow_next_call")
        }

        /**
         * Observes the current bike mode state.
         */
        fun observeBikeMode(): Flow<BikeMode> =
            context.dataStore.data.map { preferences ->
                BikeMode(
                    isEnabled = preferences[PreferencesKeys.BIKE_MODE_ENABLED] ?: false,
                    autoReplyMessage =
                        preferences[PreferencesKeys.AUTO_REPLY_MESSAGE]
                            ?: BikeMode.DEFAULT_AUTO_REPLY,
                )
            }

        /**
         * Gets the current bike mode state.
         */
        suspend fun getBikeMode(): BikeMode {
            val preferences = context.dataStore.data.first()
            return BikeMode(
                isEnabled = preferences[PreferencesKeys.BIKE_MODE_ENABLED] ?: false,
                autoReplyMessage =
                    preferences[PreferencesKeys.AUTO_REPLY_MESSAGE]
                        ?: BikeMode.DEFAULT_AUTO_REPLY,
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

        /**
         * Checks if onboarding has been completed.
         */
        fun observeOnboardingCompleted(): Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
            }

        /**
         * Gets whether onboarding has been completed.
         */
        suspend fun isOnboardingCompleted(): Boolean {
            val preferences = context.dataStore.data.first()
            return preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

        /**
         * Sets onboarding as completed.
         */
        suspend fun setOnboardingCompleted(completed: Boolean = true) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
            }
        }

        // ==================== Repeated Caller Detection ====================

        /**
         * Observes the repeated caller configuration.
         */
        fun observeRepeatedCallerConfig(): Flow<RepeatedCallerConfig> =
            context.dataStore.data.map { preferences ->
                RepeatedCallerConfig(
                    isEnabled = preferences[PreferencesKeys.REPEATED_CALLER_ENABLED] ?: true,
                    callThreshold =
                        preferences[PreferencesKeys.REPEATED_CALLER_THRESHOLD]
                            ?: RepeatedCallerConfig.DEFAULT_CALL_THRESHOLD,
                    timeWindowMinutes =
                        preferences[PreferencesKeys.REPEATED_CALLER_TIME_WINDOW]
                            ?: RepeatedCallerConfig.DEFAULT_TIME_WINDOW_MINUTES,
                )
            }

        /**
         * Gets the current repeated caller configuration.
         */
        suspend fun getRepeatedCallerConfig(): RepeatedCallerConfig {
            val preferences = context.dataStore.data.first()
            return RepeatedCallerConfig(
                isEnabled = preferences[PreferencesKeys.REPEATED_CALLER_ENABLED] ?: true,
                callThreshold =
                    preferences[PreferencesKeys.REPEATED_CALLER_THRESHOLD]
                        ?: RepeatedCallerConfig.DEFAULT_CALL_THRESHOLD,
                timeWindowMinutes =
                    preferences[PreferencesKeys.REPEATED_CALLER_TIME_WINDOW]
                        ?: RepeatedCallerConfig.DEFAULT_TIME_WINDOW_MINUTES,
            )
        }

        /**
         * Sets whether repeated caller detection is enabled.
         */
        suspend fun setRepeatedCallerEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.REPEATED_CALLER_ENABLED] = enabled
            }
        }

        /**
         * Sets the repeated caller threshold.
         */
        suspend fun setRepeatedCallerThreshold(threshold: Int) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.REPEATED_CALLER_THRESHOLD] = threshold
            }
        }

        /**
         * Sets the repeated caller time window in minutes.
         */
        suspend fun setRepeatedCallerTimeWindow(minutes: Int) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.REPEATED_CALLER_TIME_WINDOW] = minutes
            }
        }

        // ==================== Emergency Contacts ====================

        /**
         * Observes whether emergency contacts bypass is enabled.
         */
        fun observeEmergencyContactsEnabled(): Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[PreferencesKeys.EMERGENCY_CONTACTS_ENABLED] ?: true
            }

        /**
         * Gets whether emergency contacts bypass is enabled.
         */
        suspend fun isEmergencyContactsEnabled(): Boolean {
            val preferences = context.dataStore.data.first()
            return preferences[PreferencesKeys.EMERGENCY_CONTACTS_ENABLED] ?: true
        }

        /**
         * Sets whether emergency contacts bypass is enabled.
         */
        suspend fun setEmergencyContactsEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.EMERGENCY_CONTACTS_ENABLED] = enabled
            }
        }

        // ==================== Allow Next Call ====================

        /**
         * Gets whether the next call should be allowed through.
         */
        suspend fun shouldAllowNextCall(): Boolean {
            val preferences = context.dataStore.data.first()
            return preferences[PreferencesKeys.ALLOW_NEXT_CALL] ?: false
        }

        /**
         * Sets whether the next call should be allowed through.
         * This is automatically reset after a call is allowed.
         */
        suspend fun setAllowNextCall(allow: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.ALLOW_NEXT_CALL] = allow
            }
        }

        /**
         * Consumes the allow next call flag (gets and resets it).
         *
         * @return true if the next call should be allowed
         */
        suspend fun consumeAllowNextCall(): Boolean {
            val shouldAllow = shouldAllowNextCall()
            if (shouldAllow) {
                setAllowNextCall(false)
            }
            return shouldAllow
        }
    }
