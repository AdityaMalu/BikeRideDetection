package com.example.bikeridedetection.domain.model

/**
 * Represents the result of sending an SMS.
 */
sealed class SmsResult {
    /**
     * SMS was sent successfully.
     *
     * @property phoneNumber The recipient phone number
     */
    data class Sent(
        val phoneNumber: String,
    ) : SmsResult()

    /**
     * SMS sending failed.
     *
     * @property phoneNumber The recipient phone number
     * @property error The error that occurred
     */
    data class Failed(
        val phoneNumber: String,
        val error: Throwable,
    ) : SmsResult()

    /**
     * SMS was not sent because the phone number was invalid.
     */
    data object InvalidNumber : SmsResult()
}
