package com.example.bikeridedetection.data.datasource

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("MagicNumber")
class RecentCallsCacheTest {
    private lateinit var cache: RecentCallsCache

    @Before
    fun setup() {
        cache = RecentCallsCache()
    }

    @Test
    fun `recordRejectedCall_singleCall_incrementsCount`() {
        cache.recordRejectedCall("+1234567890")

        val count = cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L)

        assertEquals(1, count)
    }

    @Test
    fun `recordRejectedCall_multipleCalls_incrementsCount`() {
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+1234567890")

        val count = cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L)

        assertEquals(3, count)
    }

    @Test
    fun `getRecentCallCount_differentNumbers_trackedSeparately`() {
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+0987654321")

        val count1 = cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L)
        val count2 = cache.getRecentCallCount("+0987654321", 5 * 60 * 1000L)

        assertEquals(2, count1)
        assertEquals(1, count2)
    }

    @Test
    fun `getRecentCallCount_unknownNumber_returnsZero`() {
        val count = cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L)

        assertEquals(0, count)
    }

    @Test
    fun `shouldAllowRepeatedCaller_belowThreshold_returnsFalse`() {
        cache.recordRejectedCall("+1234567890")

        val shouldAllow = cache.shouldAllowRepeatedCaller("+1234567890", 3, 5)

        assertFalse(shouldAllow)
    }

    @Test
    fun `shouldAllowRepeatedCaller_atThreshold_returnsTrue`() {
        // Record 2 calls (threshold - 1), so the 3rd call should be allowed
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+1234567890")

        val shouldAllow = cache.shouldAllowRepeatedCaller("+1234567890", 3, 5)

        assertTrue(shouldAllow)
    }

    @Test
    fun `shouldAllowRepeatedCaller_aboveThreshold_returnsTrue`() {
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+1234567890")

        val shouldAllow = cache.shouldAllowRepeatedCaller("+1234567890", 3, 5)

        assertTrue(shouldAllow)
    }

    @Test
    fun `clear_removesAllData`() {
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+0987654321")

        cache.clear()

        assertEquals(0, cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L))
        assertEquals(0, cache.getRecentCallCount("+0987654321", 5 * 60 * 1000L))
    }

    @Test
    fun `clearForNumber_removesOnlySpecificNumber`() {
        cache.recordRejectedCall("+1234567890")
        cache.recordRejectedCall("+0987654321")

        cache.clearForNumber("+1234567890")

        assertEquals(0, cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L))
        assertEquals(1, cache.getRecentCallCount("+0987654321", 5 * 60 * 1000L))
    }

    @Test
    fun `getRecentCallCount_expiredCalls_notCounted`() {
        // Record a call with an old timestamp (6 minutes ago)
        val oldTimestamp = System.currentTimeMillis() - (6 * 60 * 1000L)
        cache.recordRejectedCall("+1234567890", oldTimestamp)

        // 5 minute window should not include the old call
        val count = cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L)

        assertEquals(0, count)
    }

    @Test
    fun `normalizePhoneNumber_handlesFormatting`() {
        cache.recordRejectedCall("+1 (234) 567-890")

        // Should match normalized version
        val count = cache.getRecentCallCount("+1234567890", 5 * 60 * 1000L)

        assertEquals(1, count)
    }
}
