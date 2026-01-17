package com.example.bikeridedetection.domain.usecase

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
         *
         * @param message The new auto-reply message
         */
        suspend operator fun invoke(message: String) {
            repository.setAutoReplyMessage(message)
        }
    }
