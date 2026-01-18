package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBikeModeUseCaseTest {
    private lateinit var repository: BikeModeRepository
    private lateinit var useCase: GetBikeModeUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetBikeModeUseCase(repository)
    }

    @Test
    fun `invoke_bikeModeEnabled_returnsBikeModeWithEnabledTrue`() =
        runTest {
            val expectedBikeMode = BikeMode(isEnabled = true, autoReplyMessage = "Test message")
            coEvery { repository.getBikeMode() } returns expectedBikeMode

            val result = useCase()

            assertEquals(expectedBikeMode, result)
            coVerify { repository.getBikeMode() }
        }

    @Test
    fun `invoke_bikeModeDisabled_returnsBikeModeWithEnabledFalse`() =
        runTest {
            val expectedBikeMode = BikeMode(isEnabled = false, autoReplyMessage = BikeMode.DEFAULT_AUTO_REPLY)
            coEvery { repository.getBikeMode() } returns expectedBikeMode

            val result = useCase()

            assertEquals(expectedBikeMode, result)
            assertEquals(false, result.isEnabled)
        }

    @Test
    fun `invoke_customAutoReplyMessage_returnsBikeModeWithCustomMessage`() =
        runTest {
            val customMessage = "Currently cycling, will call back soon!"
            val expectedBikeMode = BikeMode(isEnabled = true, autoReplyMessage = customMessage)
            coEvery { repository.getBikeMode() } returns expectedBikeMode

            val result = useCase()

            assertEquals(customMessage, result.autoReplyMessage)
        }

    @Test
    fun `invoke_defaultAutoReplyMessage_returnsBikeModeWithDefaultMessage`() =
        runTest {
            val expectedBikeMode = BikeMode(isEnabled = false)
            coEvery { repository.getBikeMode() } returns expectedBikeMode

            val result = useCase()

            assertEquals(BikeMode.DEFAULT_AUTO_REPLY, result.autoReplyMessage)
        }
}
