package com.example.bikeridedetection.ui.viewmodel

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.usecase.ObserveBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.SetBikeModeEnabledUseCase
import com.example.bikeridedetection.domain.usecase.ToggleBikeModeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var observeBikeModeUseCase: ObserveBikeModeUseCase
    private lateinit var setBikeModeEnabledUseCase: SetBikeModeEnabledUseCase
    private lateinit var toggleBikeModeUseCase: ToggleBikeModeUseCase
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        observeBikeModeUseCase = mockk()
        setBikeModeEnabledUseCase = mockk()
        toggleBikeModeUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have bike mode disabled`() =
        runTest {
            every { observeBikeModeUseCase() } returns flowOf(BikeMode(isEnabled = false))

            viewModel =
                MainViewModel(
                    observeBikeModeUseCase,
                    setBikeModeEnabledUseCase,
                    toggleBikeModeUseCase,
                )

            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(viewModel.uiState.value.bikeMode.isEnabled)
        }

    @Test
    fun `should update state when bike mode changes`() =
        runTest {
            every { observeBikeModeUseCase() } returns flowOf(BikeMode(isEnabled = true))

            viewModel =
                MainViewModel(
                    observeBikeModeUseCase,
                    setBikeModeEnabledUseCase,
                    toggleBikeModeUseCase,
                )

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.uiState.value.bikeMode.isEnabled)
        }

    @Test
    fun `setBikeModeEnabled should call use case`() =
        runTest {
            every { observeBikeModeUseCase() } returns flowOf(BikeMode(isEnabled = false))
            coEvery { setBikeModeEnabledUseCase(any()) } returns Unit

            viewModel =
                MainViewModel(
                    observeBikeModeUseCase,
                    setBikeModeEnabledUseCase,
                    toggleBikeModeUseCase,
                )

            viewModel.setBikeModeEnabled(true)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { setBikeModeEnabledUseCase(true) }
        }

    @Test
    fun `toggleBikeMode should call use case`() =
        runTest {
            every { observeBikeModeUseCase() } returns flowOf(BikeMode(isEnabled = false))
            coEvery { toggleBikeModeUseCase() } returns Unit

            viewModel =
                MainViewModel(
                    observeBikeModeUseCase,
                    setBikeModeEnabledUseCase,
                    toggleBikeModeUseCase,
                )

            viewModel.toggleBikeMode()
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { toggleBikeModeUseCase() }
        }

    @Test
    fun `clearError should clear error message`() =
        runTest {
            every { observeBikeModeUseCase() } returns flowOf(BikeMode(isEnabled = false))
            coEvery { setBikeModeEnabledUseCase(any()) } throws RuntimeException("Test error")

            viewModel =
                MainViewModel(
                    observeBikeModeUseCase,
                    setBikeModeEnabledUseCase,
                    toggleBikeModeUseCase,
                )

            viewModel.setBikeModeEnabled(true)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.clearError()

            assertEquals(null, viewModel.uiState.value.errorMessage)
        }
}
