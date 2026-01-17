package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the current bike mode state.
 */
class ObserveBikeModeUseCase
    @Inject
    constructor(
        private val repository: BikeModeRepository,
    ) {
        /**
         * Observes the current bike mode state.
         *
         * @return A Flow emitting the current BikeMode state
         */
        operator fun invoke(): Flow<BikeMode> = repository.observeBikeMode()
    }
