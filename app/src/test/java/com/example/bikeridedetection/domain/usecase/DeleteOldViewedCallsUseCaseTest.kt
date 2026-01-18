package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteOldViewedCallsUseCaseTest {
    private lateinit var repository: CallHistoryRepository
    private lateinit var useCase: DeleteOldViewedCallsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteOldViewedCallsUseCase(repository)
    }

    @Test
    fun `invoke_noOldEntries_returnsZero`() =
        runTest {
            coEvery { repository.deleteOldViewedEntries() } returns 0

            val result = useCase()

            assertEquals(0, result)
            coVerify { repository.deleteOldViewedEntries() }
        }

    @Test
    fun `invoke_withOldEntries_returnsDeletedCount`() =
        runTest {
            coEvery { repository.deleteOldViewedEntries() } returns 5

            val result = useCase()

            assertEquals(5, result)
        }

    @Test
    fun `invoke_singleOldEntry_returnsOne`() =
        runTest {
            coEvery { repository.deleteOldViewedEntries() } returns 1

            val result = useCase()

            assertEquals(1, result)
        }

    @Test
    fun `invoke_manyOldEntries_returnsCorrectCount`() =
        runTest {
            coEvery { repository.deleteOldViewedEntries() } returns 100

            val result = useCase()

            assertEquals(100, result)
        }
}

