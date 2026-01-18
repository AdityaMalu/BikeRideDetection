package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveCallHistoryUseCaseTest {
    private lateinit var repository: CallHistoryRepository
    private lateinit var useCase: SaveCallHistoryUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SaveCallHistoryUseCase(repository)
    }

    @Test
    fun `invoke_validEntry_savesAndReturnsId`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            val result = useCase(
                phoneNumber = "+1234567890",
                isFromContact = true,
                autoReplyMessage = "Test message",
            )

            assertEquals(1L, result)
            coVerify { repository.saveEntry(any()) }
        }

    @Test
    fun `invoke_fromContact_setsIsFromContactTrue`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            useCase(
                phoneNumber = "+1234567890",
                isFromContact = true,
                autoReplyMessage = "Test message",
            )

            assertTrue(entrySlot.captured.isFromContact)
        }

    @Test
    fun `invoke_notFromContact_setsIsFromContactFalse`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            useCase(
                phoneNumber = "+1234567890",
                isFromContact = false,
                autoReplyMessage = "Test message",
            )

            assertFalse(entrySlot.captured.isFromContact)
        }

    @Test
    fun `invoke_setsCorrectPhoneNumber`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            useCase(
                phoneNumber = "+9876543210",
                isFromContact = false,
                autoReplyMessage = "Test message",
            )

            assertEquals("+9876543210", entrySlot.captured.phoneNumber)
        }

    @Test
    fun `invoke_setsCorrectAutoReplyMessage`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            val customMessage = "Currently cycling, will call back soon!"
            useCase(
                phoneNumber = "+1234567890",
                isFromContact = false,
                autoReplyMessage = customMessage,
            )

            assertEquals(customMessage, entrySlot.captured.autoReplyMessage)
        }

    @Test
    fun `invoke_setsTimestamp`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            val beforeTime = System.currentTimeMillis()
            useCase(
                phoneNumber = "+1234567890",
                isFromContact = false,
                autoReplyMessage = "Test message",
            )
            val afterTime = System.currentTimeMillis()

            assertTrue(entrySlot.captured.timestamp >= beforeTime)
            assertTrue(entrySlot.captured.timestamp <= afterTime)
        }

    @Test
    fun `invoke_newEntry_hasIdZero`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            useCase(
                phoneNumber = "+1234567890",
                isFromContact = false,
                autoReplyMessage = "Test message",
            )

            assertEquals(0L, entrySlot.captured.id)
        }

    @Test
    fun `invoke_newEntry_isNotViewed`() =
        runTest {
            val entrySlot = slot<CallHistoryEntry>()
            coEvery { repository.saveEntry(capture(entrySlot)) } returns 1L

            useCase(
                phoneNumber = "+1234567890",
                isFromContact = false,
                autoReplyMessage = "Test message",
            )

            assertFalse(entrySlot.captured.isViewed)
        }
}

