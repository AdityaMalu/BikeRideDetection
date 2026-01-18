package com.example.bikeridedetection.ui.viewmodel

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.model.RepeatedCallerConfig
import com.example.bikeridedetection.domain.usecase.ObserveBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.UpdateAutoReplyUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var observeBikeModeUseCase: ObserveBikeModeUseCase
    private lateinit var updateAutoReplyUseCase: UpdateAutoReplyUseCase
    private lateinit var bikeModeDataStore: BikeModeDataStore
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        observeBikeModeUseCase = mockk()
        updateAutoReplyUseCase = mockk()
        bikeModeDataStore = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SettingsViewModel {
        every { observeBikeModeUseCase() } returns flowOf(BikeMode(isEnabled = false))
        every { bikeModeDataStore.observeRepeatedCallerConfig() } returns flowOf(RepeatedCallerConfig())
        every { bikeModeDataStore.observeEmergencyContactsEnabled() } returns flowOf(true)
        return SettingsViewModel(observeBikeModeUseCase, updateAutoReplyUseCase, bikeModeDataStore)
    }

    @Test
    fun `initial state should have default values`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(BikeMode.DEFAULT_AUTO_REPLY, state.autoReplyMessage)
            assertNull(state.autoReplyValidationError)
            assertTrue(state.isAutoDetectEnabled)
            assertTrue(state.isCallBlockingEnabled)
            assertTrue(state.isSmsAutoReplyEnabled)
            assertFalse(state.isSaving)
            assertFalse(state.saveSuccess)
            assertNull(state.errorMessage)
        }

    @Test
    fun `updateAutoReplyMessage should update state and clear validation error`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val newMessage = "Custom auto-reply message"
            viewModel.updateAutoReplyMessage(newMessage)

            assertEquals(newMessage, viewModel.uiState.value.autoReplyMessage)
            assertNull(viewModel.uiState.value.autoReplyValidationError)
        }

    @Test
    fun `saveSettings should call use case and set success for valid message`() =
        runTest {
            viewModel = createViewModel()
            coEvery { updateAutoReplyUseCase(any()) } returns true
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.saveSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateAutoReplyUseCase(any()) }
            assertTrue(viewModel.uiState.value.saveSuccess)
            assertFalse(viewModel.uiState.value.isSaving)
            assertNull(viewModel.uiState.value.autoReplyValidationError)
        }

    @Test
    fun `saveSettings should show validation warning when message is empty`() =
        runTest {
            viewModel = createViewModel()
            coEvery { updateAutoReplyUseCase(any()) } returns false
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.updateAutoReplyMessage("")
            viewModel.saveSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.uiState.value.saveSuccess)
            assertEquals("Empty message replaced with default", viewModel.uiState.value.autoReplyValidationError)
            assertEquals(BikeMode.DEFAULT_AUTO_REPLY, viewModel.uiState.value.autoReplyMessage)
        }

    @Test
    fun `saveSettings should set error on failure`() =
        runTest {
            viewModel = createViewModel()
            coEvery { updateAutoReplyUseCase(any()) } throws RuntimeException("Save failed")
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.saveSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("Failed to save settings", viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isSaving)
        }

    @Test
    fun `toggleAutoDetect should update state`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleAutoDetect(false)
            assertFalse(viewModel.uiState.value.isAutoDetectEnabled)

            viewModel.toggleAutoDetect(true)
            assertTrue(viewModel.uiState.value.isAutoDetectEnabled)
        }

    @Test
    fun `toggleCallBlocking should update state`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleCallBlocking(false)
            assertFalse(viewModel.uiState.value.isCallBlockingEnabled)
        }

    @Test
    fun `toggleSmsAutoReply should update state`() =
        runTest {
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleSmsAutoReply(false)
            assertFalse(viewModel.uiState.value.isSmsAutoReplyEnabled)
        }

    @Test
    fun `clearError should clear error message`() =
        runTest {
            viewModel = createViewModel()
            coEvery { updateAutoReplyUseCase(any()) } throws RuntimeException("Error")
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.saveSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.clearError()
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `clearSaveSuccess should clear success flag`() =
        runTest {
            viewModel = createViewModel()
            coEvery { updateAutoReplyUseCase(any()) } returns true
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.saveSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.clearSaveSuccess()
            assertFalse(viewModel.uiState.value.saveSuccess)
        }

    @Test
    fun `clearAutoReplyValidationError should clear validation error`() =
        runTest {
            viewModel = createViewModel()
            coEvery { updateAutoReplyUseCase(any()) } returns false
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.updateAutoReplyMessage("")
            viewModel.saveSettings()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.clearAutoReplyValidationError()
            assertNull(viewModel.uiState.value.autoReplyValidationError)
        }
}
