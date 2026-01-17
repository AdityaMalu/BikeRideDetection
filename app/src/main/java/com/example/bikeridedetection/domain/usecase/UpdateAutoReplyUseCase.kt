package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import javax.inject.Inject

/**
 * Use case for updating the auto-reply message.
 */
class UpdateAutoReplyUseCase
    @Inject
    constructor(
        private val repository: BikeModeRepository,
    ) {
        /**
         * Updates the auto-reply message.
         * If the message is empty or contains only whitespace, the default message is used.
         *
         * @param message The new auto-reply message
         * @return true if the provided message was valid, false if the default was used
         */
        suspend operator fun invoke(message: String): Boolean {
            val trimmedMessage = message.trim()
            return if (trimmedMessage.isBlank()) {
                repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY)
                false
            } else {
                repository.setAutoReplyMessage(trimmedMessage)
                true
            }
        }
    }
