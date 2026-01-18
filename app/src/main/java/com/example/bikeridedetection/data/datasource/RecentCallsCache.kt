package com.example.bikeridedetection.data.datasource

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory cache for tracking recent rejected calls.
 * Used for repeated caller detection to allow urgent calls through.
 */
@Singleton
class RecentCallsCache
    @Inject
    constructor() {
        private val recentCalls = mutableMapOf<String, MutableList<Long>>()

        /**
         * Records a rejected call from a phone number.
         *
         * @param phoneNumber The phone number that was rejected
         * @param timestamp The timestamp of the call (defaults to current time)
         */
        @Synchronized
        fun recordRejectedCall(
            phoneNumber: String,
            timestamp: Long = System.currentTimeMillis(),
        ) {
            val normalizedNumber = normalizePhoneNumber(phoneNumber)
            val calls = recentCalls.getOrPut(normalizedNumber) { mutableListOf() }
            calls.add(timestamp)
            Timber.d("Recorded rejected call from $normalizedNumber at $timestamp")
        }

        /**
         * Gets the count of recent calls from a phone number within the time window.
         *
         * @param phoneNumber The phone number to check
         * @param timeWindowMillis The time window in milliseconds
         * @return The number of calls within the time window
         */
        @Synchronized
        fun getRecentCallCount(
            phoneNumber: String,
            timeWindowMillis: Long,
        ): Int {
            val normalizedNumber = normalizePhoneNumber(phoneNumber)
            val calls = recentCalls[normalizedNumber] ?: return 0
            val cutoffTime = System.currentTimeMillis() - timeWindowMillis

            // Clean up old entries
            calls.removeAll { it < cutoffTime }

            // Remove entry if empty
            if (calls.isEmpty()) {
                recentCalls.remove(normalizedNumber)
            }

            val count = calls.size
            Timber.d("Recent call count for $normalizedNumber: $count (window: ${timeWindowMillis}ms)")
            return count
        }

        /**
         * Checks if a caller should be allowed through based on repeated call detection.
         *
         * @param phoneNumber The phone number to check
         * @param callThreshold Number of calls required to allow through
         * @param timeWindowMinutes Time window in minutes
         * @return true if the caller should be allowed through
         */
        @Synchronized
        fun shouldAllowRepeatedCaller(
            phoneNumber: String,
            callThreshold: Int,
            timeWindowMinutes: Int,
        ): Boolean {
            val timeWindowMillis = timeWindowMinutes * 60 * 1000L
            val recentCount = getRecentCallCount(phoneNumber, timeWindowMillis)

            // The current call is not yet recorded, so we check if count >= threshold - 1
            // This means if threshold is 3, we allow on the 3rd call (after 2 previous rejections)
            val shouldAllow = recentCount >= callThreshold - 1
            Timber.d(
                "Repeated caller check for $phoneNumber: count=$recentCount, " +
                    "threshold=$callThreshold, shouldAllow=$shouldAllow",
            )
            return shouldAllow
        }

        /**
         * Clears all cached call data.
         */
        @Synchronized
        fun clear() {
            recentCalls.clear()
            Timber.d("Recent calls cache cleared")
        }

        /**
         * Clears call data for a specific phone number.
         *
         * @param phoneNumber The phone number to clear
         */
        @Synchronized
        fun clearForNumber(phoneNumber: String) {
            val normalizedNumber = normalizePhoneNumber(phoneNumber)
            recentCalls.remove(normalizedNumber)
            Timber.d("Cleared recent calls for $normalizedNumber")
        }

        /**
         * Normalizes a phone number by removing common formatting characters.
         */
        private fun normalizePhoneNumber(phoneNumber: String): String = phoneNumber.replace(Regex("[\\s\\-().]"), "")
    }
