package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.BikeModeRepository
import javax.inject.Inject

/**
 * Use case for toggling bike mode on/off.
 */
class ToggleBikeModeUseCase
    @Inject
    constructor(
        private val repository: BikeModeRepository,
    ) {
        /**
         * Toggles the current bike mode state.
         */
        suspend operator fun invoke() {
            val currentMode = repository.getBikeMode()
            repository.setBikeModeEnabled(!currentMode.isEnabled)
        }
    }
