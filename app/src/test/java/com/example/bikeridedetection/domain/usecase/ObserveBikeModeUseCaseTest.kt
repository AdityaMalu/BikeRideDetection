package com.example.bikeridedetection.domain.usecase

import app.cash.turbine.test
import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveBikeModeUseCaseTest {

    private lateinit var repository: BikeModeRepository
    private lateinit var useCase: ObserveBikeModeUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveBikeModeUseCase(repository)
    }

    @Test
    fun `should emit bike mode from repository`() = runTest {
        val bikeMode = BikeMode(isEnabled = true, autoReplyMessage = "Test message")
        every { repository.observeBikeMode() } returns flowOf(bikeMode)

        useCase().test {
            assertEquals(bikeMode, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `should emit multiple bike mode updates`() = runTest {
        val bikeMode1 = BikeMode(isEnabled = false)
        val bikeMode2 = BikeMode(isEnabled = true)
        every { repository.observeBikeMode() } returns flowOf(bikeMode1, bikeMode2)

        useCase().test {
            assertEquals(bikeMode1, awaitItem())
            assertEquals(bikeMode2, awaitItem())
            awaitComplete()
        }
    }
}

