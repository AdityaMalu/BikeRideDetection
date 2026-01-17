package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.BikeModeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SetBikeModeEnabledUseCaseTest {

    private lateinit var repository: BikeModeRepository
    private lateinit var useCase: SetBikeModeEnabledUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SetBikeModeEnabledUseCase(repository)
    }

    @Test
    fun `should enable bike mode`() = runTest {
        coEvery { repository.setBikeModeEnabled(true) } returns Unit

        useCase(true)

        coVerify { repository.setBikeModeEnabled(true) }
    }

    @Test
    fun `should disable bike mode`() = runTest {
        coEvery { repository.setBikeModeEnabled(false) } returns Unit

        useCase(false)

        coVerify { repository.setBikeModeEnabled(false) }
    }
}

