package com.example.bikeridedetection.domain.model

/**
 * Represents the current state of bike mode.
 *
 * @property isEnabled Whether bike mode is currently enabled
 * @property autoReplyMessage The message to send when rejecting calls
 */
data class BikeMode(
    val isEnabled: Boolean = false,
    val autoReplyMessage: String = DEFAULT_AUTO_REPLY,
) {
    companion object {
        const val DEFAULT_AUTO_REPLY = "I'm riding my bike right now."
    }
}
