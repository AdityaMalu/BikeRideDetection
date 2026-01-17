package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import javax.inject.Inject

/**
 * Use case for getting the current bike mode state.
 */
class GetBikeModeUseCase
    @Inject
    constructor(
        private val repository: BikeModeRepository,
    ) {
        /**
         * Gets the current bike mode state.
         *
         * @return The current BikeMode state
         */
        suspend operator fun invoke(): BikeMode = repository.getBikeMode()
    }
