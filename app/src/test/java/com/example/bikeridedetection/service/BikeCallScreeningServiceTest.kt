package com.example.bikeridedetection.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for BikeCallScreeningService.
 *
 * Note: Only the static utility methods can be unit tested.
 * The service lifecycle and call screening logic require instrumentation tests
 * due to heavy Android framework dependencies (CallScreeningService, Call.Details, etc.).
 */
class BikeCallScreeningServiceTest {
    @Test
    fun `extractPhoneNumber_validTelUri_returnsPhoneNumber`() {
        val input = "tel:+1234567890"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+1234567890", result)
    }

    @Test
    fun `extractPhoneNumber_telUriWithEncodedPlus_decodesCorrectly`() {
        val input = "tel:%2B1234567890"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+1234567890", result)
    }

    @Test
    fun `extractPhoneNumber_nullInput_returnsNull`() {
        val result = BikeCallScreeningService.extractPhoneNumber(null)

        assertNull(result)
    }

    @Test
    fun `extractPhoneNumber_emptyString_returnsNull`() {
        val result = BikeCallScreeningService.extractPhoneNumber("")

        assertNull(result)
    }

    @Test
    fun `extractPhoneNumber_invalidPrefix_returnsNull`() {
        val result = BikeCallScreeningService.extractPhoneNumber("sms:+1234567890")

        assertNull(result)
    }

    @Test
    fun `extractPhoneNumber_noPrefix_returnsNull`() {
        val result = BikeCallScreeningService.extractPhoneNumber("+1234567890")

        assertNull(result)
    }

    @Test
    fun `extractPhoneNumber_telPrefixOnly_returnsEmptyString`() {
        val input = "tel:"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("", result)
    }

    @Test
    fun `extractPhoneNumber_internationalNumber_returnsCorrectNumber`() {
        val input = "tel:+44-20-7946-0958"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+44-20-7946-0958", result)
    }

    @Test
    fun `extractPhoneNumber_localNumber_returnsCorrectNumber`() {
        val input = "tel:5551234567"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("5551234567", result)
    }

    @Test
    fun `extractPhoneNumber_numberWithSpaces_preservesSpaces`() {
        val input = "tel:+1 234 567 890"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+1 234 567 890", result)
    }

    @Test
    fun `extractPhoneNumber_numberWithParentheses_preservesParentheses`() {
        val input = "tel:+1(234)567-890"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+1(234)567-890", result)
    }

    @Test
    fun `extractPhoneNumber_multipleEncodedPlus_decodesAll`() {
        val input = "tel:%2B1%2B234"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+1+234", result)
    }

    @Test
    fun `extractPhoneNumber_caseSensitivePrefix_returnsNull`() {
        // "TEL:" should not match "tel:"
        val result = BikeCallScreeningService.extractPhoneNumber("TEL:+1234567890")

        assertNull(result)
    }

    @Test
    fun `extractPhoneNumber_telWithExtraColons_preservesColons`() {
        val input = "tel:+1:234:567"

        val result = BikeCallScreeningService.extractPhoneNumber(input)

        assertEquals("+1:234:567", result)
    }
}
