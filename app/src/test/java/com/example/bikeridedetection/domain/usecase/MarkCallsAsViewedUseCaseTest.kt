package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.repository.CallHistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MarkCallsAsViewedUseCaseTest {
    private lateinit var repository: CallHistoryRepository
    private lateinit var useCase: MarkCallsAsViewedUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = MarkCallsAsViewedUseCase(repository)
    }

    @Test
    fun `invoke_callsRepositoryMarkAllAsViewed`() =
        runTest {
            coEvery { repository.markAllAsViewed() } returns Unit

            useCase()

            coVerify(exactly = 1) { repository.markAllAsViewed() }
        }

    @Test
    fun `invoke_multipleInvocations_callsRepositoryEachTime`() =
        runTest {
            coEvery { repository.markAllAsViewed() } returns Unit

            useCase()
            useCase()
            useCase()

            coVerify(exactly = 3) { repository.markAllAsViewed() }
        }
}
