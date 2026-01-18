package com.example.bikeridedetection.data.repository

import app.cash.turbine.test
import com.example.bikeridedetection.data.local.dao.CallHistoryDao
import com.example.bikeridedetection.data.local.entity.CallHistoryEntity
import com.example.bikeridedetection.domain.model.CallHistoryEntry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CallHistoryRepositoryImplTest {
    private lateinit var callHistoryDao: CallHistoryDao
    private lateinit var repository: CallHistoryRepositoryImpl

    @Before
    fun setup() {
        callHistoryDao = mockk()
        repository = CallHistoryRepositoryImpl(callHistoryDao)
    }

    @Test
    fun `saveEntry_validEntry_insertsAndReturnsId`() =
        runTest {
            val entry =
                CallHistoryEntry(
                    id = 0,
                    phoneNumber = "+1234567890",
                    timestamp = 1000L,
                    isFromContact = true,
                    autoReplyMessage = "Test message",
                )
            val entitySlot = slot<CallHistoryEntity>()
            coEvery { callHistoryDao.insert(capture(entitySlot)) } returns 1L

            val result = repository.saveEntry(entry)

            assertEquals(1L, result)
            assertEquals(entry.phoneNumber, entitySlot.captured.phoneNumber)
            assertEquals(entry.timestamp, entitySlot.captured.timestamp)
            assertEquals(entry.isFromContact, entitySlot.captured.isFromContact)
            assertEquals(entry.autoReplyMessage, entitySlot.captured.autoReplyMessage)
        }

    @Test
    fun `getAllEntries_emptyList_returnsEmptyFlow`() =
        runTest {
            every { callHistoryDao.getAllEntries() } returns flowOf(emptyList())

            repository.getAllEntries().test {
                assertEquals(emptyList<CallHistoryEntry>(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `getAllEntries_withEntries_returnsMappedDomainModels`() =
        runTest {
            val entities =
                listOf(
                    CallHistoryEntity(
                        id = 1,
                        phoneNumber = "+1234567890",
                        timestamp = 1000L,
                        isFromContact = true,
                        autoReplyMessage = "Test message",
                    ),
                    CallHistoryEntity(
                        id = 2,
                        phoneNumber = "+0987654321",
                        timestamp = 2000L,
                        isFromContact = false,
                        autoReplyMessage = "Another message",
                    ),
                )
            every { callHistoryDao.getAllEntries() } returns flowOf(entities)

            repository.getAllEntries().test {
                val result = awaitItem()
                assertEquals(2, result.size)
                assertEquals("+1234567890", result[0].phoneNumber)
                assertEquals("+0987654321", result[1].phoneNumber)
                awaitComplete()
            }
        }

    @Test
    fun `getUnviewedEntries_withUnviewedEntries_returnsOnlyUnviewed`() =
        runTest {
            val entities =
                listOf(
                    CallHistoryEntity(
                        id = 1,
                        phoneNumber = "+1234567890",
                        timestamp = 1000L,
                        isFromContact = true,
                        autoReplyMessage = "Test message",
                        isViewed = false,
                    ),
                )
            every { callHistoryDao.getUnviewedEntries() } returns flowOf(entities)

            repository.getUnviewedEntries().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals(false, result[0].isViewed)
                awaitComplete()
            }
        }

    @Test
    fun `getUnviewedCount_returnsCountFromDao`() =
        runTest {
            every { callHistoryDao.getUnviewedCount() } returns flowOf(5)

            repository.getUnviewedCount().test {
                assertEquals(5, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `markAllAsViewed_callsDaoWithTimestamp`() =
        runTest {
            val timestampSlot = slot<Long>()
            coEvery { callHistoryDao.markAllAsViewed(capture(timestampSlot)) } returns Unit

            val beforeTime = System.currentTimeMillis()
            repository.markAllAsViewed()
            val afterTime = System.currentTimeMillis()

            coVerify { callHistoryDao.markAllAsViewed(any()) }
            assert(timestampSlot.captured >= beforeTime)
            assert(timestampSlot.captured <= afterTime)
        }

    @Test
    fun `deleteOldViewedEntries_defaultRetention_callsDaoWithCorrectThreshold`() =
        runTest {
            val thresholdSlot = slot<Long>()
            coEvery { callHistoryDao.deleteOldViewedEntries(capture(thresholdSlot)) } returns 3

            val beforeTime = System.currentTimeMillis()
            val result = repository.deleteOldViewedEntries()
            val afterTime = System.currentTimeMillis()

            assertEquals(3, result)
            // Threshold should be current time minus 24 hours
            val expectedMinThreshold = beforeTime - (24 * 60 * 60 * 1000L)
            val expectedMaxThreshold = afterTime - (24 * 60 * 60 * 1000L)
            assert(thresholdSlot.captured >= expectedMinThreshold)
            assert(thresholdSlot.captured <= expectedMaxThreshold)
        }

    @Test
    fun `deleteAll_callsDao`() =
        runTest {
            coEvery { callHistoryDao.deleteAll() } returns Unit

            repository.deleteAll()

            coVerify(exactly = 1) { callHistoryDao.deleteAll() }
        }
}
