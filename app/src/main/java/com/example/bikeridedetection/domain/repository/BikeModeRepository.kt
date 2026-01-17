package com.example.bikeridedetection.domain.repository

import com.example.bikeridedetection.domain.model.BikeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing bike mode preferences.
 */
interface BikeModeRepository {
    /**
     * Observes the current bike mode state.
     *
     * @return A Flow emitting the current BikeMode state
     */
    fun observeBikeMode(): Flow<BikeMode>

    /**
     * Gets the current bike mode state.
     *
     * @return The current BikeMode state
     */
    suspend fun getBikeMode(): BikeMode

    /**
     * Sets whether bike mode is enabled.
     *
     * @param enabled Whether bike mode should be enabled
     */
    suspend fun setBikeModeEnabled(enabled: Boolean)

    /**
     * Sets the auto-reply message for bike mode.
     *
     * @param message The message to send when rejecting calls
     */
    suspend fun setAutoReplyMessage(message: String)
}

