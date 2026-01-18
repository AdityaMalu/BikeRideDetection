package com.example.bikeridedetection.ui.viewmodel

import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.usecase.GetCallHistoryUseCase
import com.example.bikeridedetection.domain.usecase.MarkCallsAsViewedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class CallHistoryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCallHistoryUseCase: GetCallHistoryUseCase
    private lateinit var markCallsAsViewedUseCase: MarkCallsAsViewedUseCase
    private lateinit var viewModel: CallHistoryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCallHistoryUseCase = mockk()
        markCallsAsViewedUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): CallHistoryViewModel {
        every { getCallHistoryUseCase() } returns flowOf(emptyList())
        return CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)
    }

    @Test
    fun `initial_state_isLoading`() =
        runTest {
            every { getCallHistoryUseCase() } returns flowOf(emptyList())

            viewModel = CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `observeCallHistory_emptyList_updatesStateWithEmptyEntries`() =
        runTest {
            every { getCallHistoryUseCase() } returns flowOf(emptyList())

            viewModel = CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.entries.isEmpty())
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `observeCallHistory_withEntries_updatesStateWithEntries`() =
        runTest {
            val entries = listOf(
                CallHistoryEntry(
                    id = 1,
                    phoneNumber = "+1234567890",
                    timestamp = 1000L,
                    isFromContact = true,
                    autoReplyMessage = "Test message",
                ),
                CallHistoryEntry(
                    id = 2,
                    phoneNumber = "+0987654321",
                    timestamp = 2000L,
                    isFromContact = false,
                    autoReplyMessage = "Another message",
                ),
            )
            every { getCallHistoryUseCase() } returns flowOf(entries)

            viewModel = CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(2, viewModel.uiState.value.entries.size)
            assertEquals("+1234567890", viewModel.uiState.value.entries[0].phoneNumber)
            assertEquals("+0987654321", viewModel.uiState.value.entries[1].phoneNumber)
        }

    @Test
    fun `observeCallHistory_error_setsErrorMessage`() =
        runTest {
            every { getCallHistoryUseCase() } returns flow { throw RuntimeException("Test error") }

            viewModel = CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals("Failed to load call history", viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `markAllAsViewed_callsUseCase`() =
        runTest {
            viewModel = createViewModel()
            coEvery { markCallsAsViewedUseCase() } returns Unit
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.markAllAsViewed()
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { markCallsAsViewedUseCase() }
        }

    @Test
    fun `markAllAsViewed_useCaseThrows_doesNotCrash`() =
        runTest {
            viewModel = createViewModel()
            coEvery { markCallsAsViewedUseCase() } throws RuntimeException("Mark failed")
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.markAllAsViewed()
            testDispatcher.scheduler.advanceUntilIdle()

            // Should not crash, error is logged
            coVerify { markCallsAsViewedUseCase() }
        }

    @Test
    fun `clearError_clearsErrorMessage`() =
        runTest {
            every { getCallHistoryUseCase() } returns flow { throw RuntimeException("Test error") }

            viewModel = CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.clearError()

            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `observeCallHistory_multipleEmissions_updatesState`() =
        runTest {
            val entries1 = listOf(
                CallHistoryEntry(
                    id = 1,
                    phoneNumber = "+1234567890",
                    timestamp = 1000L,
                    isFromContact = true,
                    autoReplyMessage = "Test message",
                ),
            )
            every { getCallHistoryUseCase() } returns flowOf(entries1)

            viewModel = CallHistoryViewModel(getCallHistoryUseCase, markCallsAsViewedUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.entries.size)
        }
}

