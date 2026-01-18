package com.example.bikeridedetection.service

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for BikeTransitionReceiver.
 *
 * Note: Only the companion object constants can be unit tested.
 * The BroadcastReceiver logic requires instrumentation tests due to
 * Android framework dependencies (Context, Intent, ActivityTransitionResult, etc.).
 */
class BikeTransitionReceiverTest {
    @Test
    fun `ACTION_BIKE_MODE_CHANGED_hasCorrectValue`() {
        assertEquals(
            "com.example.bikeridedetection.BIKE_MODE_CHANGED",
            BikeTransitionReceiver.ACTION_BIKE_MODE_CHANGED,
        )
    }

    @Test
    fun `KEY_BIKE_MODE_hasCorrectValue`() {
        assertEquals("bike_mode", BikeTransitionReceiver.KEY_BIKE_MODE)
    }

    @Test
    fun `ACTION_BIKE_MODE_CHANGED_isNotEmpty`() {
        assert(BikeTransitionReceiver.ACTION_BIKE_MODE_CHANGED.isNotEmpty())
    }

    @Test
    fun `KEY_BIKE_MODE_isNotEmpty`() {
        assert(BikeTransitionReceiver.KEY_BIKE_MODE.isNotEmpty())
    }

    @Test
    fun `ACTION_BIKE_MODE_CHANGED_containsPackageName`() {
        assert(BikeTransitionReceiver.ACTION_BIKE_MODE_CHANGED.contains("bikeridedetection"))
    }
}
