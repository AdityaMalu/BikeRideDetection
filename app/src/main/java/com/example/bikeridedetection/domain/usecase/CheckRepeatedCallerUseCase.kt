package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.data.datasource.RecentCallsCache
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for checking if a caller should be allowed through based on repeated call detection.
 */
class CheckRepeatedCallerUseCase
    @Inject
    constructor(
        private val recentCallsCache: RecentCallsCache,
        private val bikeModeDataStore: BikeModeDataStore,
    ) {
        /**
         * Checks if a caller should be allowed through based on repeated call detection.
         * This does NOT record the call - use [recordRejectedCall] for that.
         *
         * @param phoneNumber The phone number to check
         * @return true if the caller should be allowed through
         */
        suspend operator fun invoke(phoneNumber: String): Boolean {
            val config = bikeModeDataStore.getRepeatedCallerConfig()

            if (!config.isEnabled) {
                Timber.d("Repeated caller detection is disabled")
                return false
            }

            val shouldAllow =
                recentCallsCache.shouldAllowRepeatedCaller(
                    phoneNumber = phoneNumber,
                    callThreshold = config.callThreshold,
                    timeWindowMinutes = config.timeWindowMinutes,
                )

            Timber.d(
                "Repeated caller check for $phoneNumber: shouldAllow=$shouldAllow " +
                    "(threshold=${config.callThreshold}, window=${config.timeWindowMinutes}min)",
            )

            return shouldAllow
        }

        /**
         * Records a rejected call for repeated caller tracking.
         *
         * @param phoneNumber The phone number that was rejected
         */
        fun recordRejectedCall(phoneNumber: String) {
            recentCallsCache.recordRejectedCall(phoneNumber)
        }

        /**
         * Clears the repeated caller tracking for a specific number.
         * Called when a call is allowed through.
         *
         * @param phoneNumber The phone number to clear
         */
        fun clearForNumber(phoneNumber: String) {
            recentCallsCache.clearForNumber(phoneNumber)
        }
    }
