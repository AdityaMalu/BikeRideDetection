package com.example.bikeridedetection.domain.usecase

import app.cash.turbine.test
import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCallHistoryUseCaseTest {
    private lateinit var repository: CallHistoryRepository
    private lateinit var useCase: GetCallHistoryUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCallHistoryUseCase(repository)
    }

    @Test
    fun `invoke_emptyHistory_returnsEmptyList`() =
        runTest {
            every { repository.getAllEntries() } returns flowOf(emptyList())

            useCase().test {
                assertEquals(emptyList<CallHistoryEntry>(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `invoke_withEntries_returnsAllEntries`() =
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
            every { repository.getAllEntries() } returns flowOf(entries)

            useCase().test {
                assertEquals(entries, awaitItem())
                awaitComplete()
            }
            verify { repository.getAllEntries() }
        }

    @Test
    fun `invoke_multipleEmissions_emitsAllUpdates`() =
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
            val entries2 = entries1 + CallHistoryEntry(
                id = 2,
                phoneNumber = "+0987654321",
                timestamp = 2000L,
                isFromContact = false,
                autoReplyMessage = "Another message",
            )
            every { repository.getAllEntries() } returns flowOf(entries1, entries2)

            useCase().test {
                assertEquals(entries1, awaitItem())
                assertEquals(entries2, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `getUnviewedCount_noUnviewedEntries_returnsZero`() =
        runTest {
            every { repository.getUnviewedCount() } returns flowOf(0)

            useCase.getUnviewedCount().test {
                assertEquals(0, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `getUnviewedCount_withUnviewedEntries_returnsCount`() =
        runTest {
            every { repository.getUnviewedCount() } returns flowOf(5)

            useCase.getUnviewedCount().test {
                assertEquals(5, awaitItem())
                awaitComplete()
            }
            verify { repository.getUnviewedCount() }
        }

    @Test
    fun `getUnviewedCount_countChanges_emitsUpdates`() =
        runTest {
            every { repository.getUnviewedCount() } returns flowOf(3, 5, 2)

            useCase.getUnviewedCount().test {
                assertEquals(3, awaitItem())
                assertEquals(5, awaitItem())
                assertEquals(2, awaitItem())
                awaitComplete()
            }
        }
}

