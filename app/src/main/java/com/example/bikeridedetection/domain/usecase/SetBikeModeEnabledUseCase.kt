package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.BikeModeRepository
import javax.inject.Inject

/**
 * Use case for enabling or disabling bike mode.
 */
class SetBikeModeEnabledUseCase @Inject constructor(
    private val repository: BikeModeRepository
) {
    /**
     * Sets whether bike mode is enabled.
     *
     * @param enabled Whether bike mode should be enabled
     */
    suspend operator fun invoke(enabled: Boolean) {
        repository.setBikeModeEnabled(enabled)
    }
}

