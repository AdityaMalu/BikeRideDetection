package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.data.datasource.RecentCallsCache
import com.example.bikeridedetection.domain.model.RepeatedCallerConfig
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("MagicNumber")
class CheckRepeatedCallerUseCaseTest {
    private lateinit var recentCallsCache: RecentCallsCache
    private lateinit var bikeModeDataStore: BikeModeDataStore
    private lateinit var useCase: CheckRepeatedCallerUseCase

    @Before
    fun setup() {
        recentCallsCache = mockk(relaxed = true)
        bikeModeDataStore = mockk()
        useCase = CheckRepeatedCallerUseCase(recentCallsCache, bikeModeDataStore)
    }

    @Test
    fun `invoke_featureDisabled_returnsFalse`() =
        runTest {
            coEvery { bikeModeDataStore.getRepeatedCallerConfig() } returns
                RepeatedCallerConfig(isEnabled = false)

            val result = useCase("+1234567890")

            assertFalse(result)
        }

    @Test
    fun `invoke_featureEnabled_belowThreshold_returnsFalse`() =
        runTest {
            coEvery { bikeModeDataStore.getRepeatedCallerConfig() } returns
                RepeatedCallerConfig(isEnabled = true, callThreshold = 3, timeWindowMinutes = 5)
            every {
                recentCallsCache.shouldAllowRepeatedCaller("+1234567890", 3, 5)
            } returns false

            val result = useCase("+1234567890")

            assertFalse(result)
        }

    @Test
    fun `invoke_featureEnabled_atThreshold_returnsTrue`() =
        runTest {
            coEvery { bikeModeDataStore.getRepeatedCallerConfig() } returns
                RepeatedCallerConfig(isEnabled = true, callThreshold = 3, timeWindowMinutes = 5)
            every {
                recentCallsCache.shouldAllowRepeatedCaller("+1234567890", 3, 5)
            } returns true

            val result = useCase("+1234567890")

            assertTrue(result)
        }

    @Test
    fun `recordRejectedCall_callsCache`() {
        useCase.recordRejectedCall("+1234567890")

        verify { recentCallsCache.recordRejectedCall("+1234567890", any()) }
    }

    @Test
    fun `clearForNumber_callsCache`() {
        useCase.clearForNumber("+1234567890")

        verify { recentCallsCache.clearForNumber("+1234567890") }
    }
}
