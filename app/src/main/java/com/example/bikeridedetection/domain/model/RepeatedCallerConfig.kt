package com.example.bikeridedetection.domain.model

/**
 * Configuration for repeated caller detection.
 * If the same number calls multiple times within the time window,
 * the call will be allowed through as it may be urgent.
 *
 * @property isEnabled Whether repeated caller detection is enabled
 * @property callThreshold Number of calls required to allow through (default: 3)
 * @property timeWindowMinutes Time window in minutes to count calls (default: 5)
 */
data class RepeatedCallerConfig(
    val isEnabled: Boolean = true,
    val callThreshold: Int = DEFAULT_CALL_THRESHOLD,
    val timeWindowMinutes: Int = DEFAULT_TIME_WINDOW_MINUTES,
) {
    companion object {
        const val DEFAULT_CALL_THRESHOLD = 3
        const val DEFAULT_TIME_WINDOW_MINUTES = 5
    }
}
