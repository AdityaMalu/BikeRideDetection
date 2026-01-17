package com.example.bikeridedetection.domain.repository

import com.example.bikeridedetection.domain.model.SmsResult

/**
 * Repository interface for sending SMS messages.
 */
interface SmsRepository {
    /**
     * Sends an SMS message to the specified phone number.
     *
     * @param phoneNumber The recipient phone number
     * @param message The message to send
     * @return The result of the SMS operation
     */
    suspend fun sendSms(
        phoneNumber: String,
        message: String,
    ): SmsResult
}
