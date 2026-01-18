package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for managing the "allow next call" feature.
 * This allows users to temporarily bypass call blocking for the next incoming call.
 */
class AllowNextCallUseCase
    @Inject
    constructor(
        private val bikeModeDataStore: BikeModeDataStore,
    ) {
        /**
         * Sets the flag to allow the next incoming call.
         */
        suspend fun enable() {
            Timber.d("Enabling allow next call")
            bikeModeDataStore.setAllowNextCall(true)
        }

        /**
         * Clears the allow next call flag.
         */
        suspend fun disable() {
            Timber.d("Disabling allow next call")
            bikeModeDataStore.setAllowNextCall(false)
        }

        /**
         * Checks if the next call should be allowed and consumes the flag.
         *
         * @return true if the next call should be allowed
         */
        suspend fun consumeIfEnabled(): Boolean {
            val shouldAllow = bikeModeDataStore.consumeAllowNextCall()
            if (shouldAllow) {
                Timber.d("Allow next call consumed - next call will be allowed")
            }
            return shouldAllow
        }

        /**
         * Checks if the allow next call flag is set without consuming it.
         *
         * @return true if the next call should be allowed
         */
        suspend fun isEnabled(): Boolean = bikeModeDataStore.shouldAllowNextCall()
    }
