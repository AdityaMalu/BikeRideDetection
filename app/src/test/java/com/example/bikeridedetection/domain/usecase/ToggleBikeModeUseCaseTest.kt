package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleBikeModeUseCaseTest {

    private lateinit var repository: BikeModeRepository
    private lateinit var useCase: ToggleBikeModeUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ToggleBikeModeUseCase(repository)
    }

    @Test
    fun `should toggle from disabled to enabled`() = runTest {
        coEvery { repository.getBikeMode() } returns BikeMode(isEnabled = false)
        coEvery { repository.setBikeModeEnabled(true) } returns Unit

        useCase()

        coVerify { repository.setBikeModeEnabled(true) }
    }

    @Test
    fun `should toggle from enabled to disabled`() = runTest {
        coEvery { repository.getBikeMode() } returns BikeMode(isEnabled = true)
        coEvery { repository.setBikeModeEnabled(false) } returns Unit

        useCase()

        coVerify { repository.setBikeModeEnabled(false) }
    }
}

