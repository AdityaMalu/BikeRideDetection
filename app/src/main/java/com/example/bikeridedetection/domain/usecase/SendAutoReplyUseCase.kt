package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.SmsResult
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import com.example.bikeridedetection.domain.repository.SmsRepository
import javax.inject.Inject

/**
 * Use case for sending an auto-reply SMS when a call is rejected.
 */
class SendAutoReplyUseCase
    @Inject
    constructor(
        private val bikeModeRepository: BikeModeRepository,
        private val smsRepository: SmsRepository,
    ) {
        /**
         * Sends an auto-reply SMS to the specified phone number.
         *
         * @param phoneNumber The recipient phone number
         * @return The result of the SMS operation
         */
        suspend operator fun invoke(phoneNumber: String): SmsResult {
            val bikeMode = bikeModeRepository.getBikeMode()
            return smsRepository.sendSms(phoneNumber, bikeMode.autoReplyMessage)
        }
    }
