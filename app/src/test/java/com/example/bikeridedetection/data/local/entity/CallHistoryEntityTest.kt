package com.example.bikeridedetection.data.local.entity

import com.example.bikeridedetection.domain.model.CallHistoryEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CallHistoryEntityTest {
    @Test
    fun `toDomainModel_allFieldsPopulated_mapsCorrectly`() {
        val entity =
            CallHistoryEntity(
                id = 1,
                phoneNumber = "+1234567890",
                timestamp = 1000L,
                isFromContact = true,
                autoReplyMessage = "Test message",
                isViewed = true,
                viewedAt = 2000L,
            )

        val domainModel = entity.toDomainModel()

        assertEquals(1L, domainModel.id)
        assertEquals("+1234567890", domainModel.phoneNumber)
        assertEquals(1000L, domainModel.timestamp)
        assertTrue(domainModel.isFromContact)
        assertEquals("Test message", domainModel.autoReplyMessage)
        assertTrue(domainModel.isViewed)
        assertEquals(2000L, domainModel.viewedAt)
    }

    @Test
    fun `toDomainModel_defaultValues_mapsCorrectly`() {
        val entity =
            CallHistoryEntity(
                phoneNumber = "+1234567890",
                timestamp = 1000L,
                isFromContact = false,
                autoReplyMessage = "Test message",
            )

        val domainModel = entity.toDomainModel()

        assertEquals(0L, domainModel.id)
        assertFalse(domainModel.isViewed)
        assertNull(domainModel.viewedAt)
    }

    @Test
    fun `toDomainModel_viewedAtNull_mapsToNull`() {
        val entity =
            CallHistoryEntity(
                id = 1,
                phoneNumber = "+1234567890",
                timestamp = 1000L,
                isFromContact = false,
                autoReplyMessage = "Test message",
                isViewed = false,
                viewedAt = null,
            )

        val domainModel = entity.toDomainModel()

        assertNull(domainModel.viewedAt)
    }

    @Test
    fun `fromDomainModel_allFieldsPopulated_mapsCorrectly`() {
        val entry =
            CallHistoryEntry(
                id = 1,
                phoneNumber = "+1234567890",
                timestamp = 1000L,
                isFromContact = true,
                autoReplyMessage = "Test message",
                isViewed = true,
                viewedAt = 2000L,
            )

        val entity = CallHistoryEntity.fromDomainModel(entry)

        assertEquals(1L, entity.id)
        assertEquals("+1234567890", entity.phoneNumber)
        assertEquals(1000L, entity.timestamp)
        assertTrue(entity.isFromContact)
        assertEquals("Test message", entity.autoReplyMessage)
        assertTrue(entity.isViewed)
        assertEquals(2000L, entity.viewedAt)
    }

    @Test
    fun `fromDomainModel_defaultValues_mapsCorrectly`() {
        val entry =
            CallHistoryEntry(
                phoneNumber = "+1234567890",
                timestamp = 1000L,
                autoReplyMessage = "Test message",
            )

        val entity = CallHistoryEntity.fromDomainModel(entry)

        assertEquals(0L, entity.id)
        assertFalse(entity.isFromContact)
        assertFalse(entity.isViewed)
        assertNull(entity.viewedAt)
    }

    @Test
    fun `roundTrip_entityToDomainAndBack_preservesAllFields`() {
        val originalEntity =
            CallHistoryEntity(
                id = 5,
                phoneNumber = "+9876543210",
                timestamp = 5000L,
                isFromContact = true,
                autoReplyMessage = "Round trip message",
                isViewed = true,
                viewedAt = 6000L,
            )

        val domainModel = originalEntity.toDomainModel()
        val resultEntity = CallHistoryEntity.fromDomainModel(domainModel)

        assertEquals(originalEntity, resultEntity)
    }

    @Test
    fun `roundTrip_domainToEntityAndBack_preservesAllFields`() {
        val originalEntry =
            CallHistoryEntry(
                id = 5,
                phoneNumber = "+9876543210",
                timestamp = 5000L,
                isFromContact = true,
                autoReplyMessage = "Round trip message",
                isViewed = true,
                viewedAt = 6000L,
            )

        val entity = CallHistoryEntity.fromDomainModel(originalEntry)
        val resultEntry = entity.toDomainModel()

        assertEquals(originalEntry, resultEntry)
    }

    @Test
    fun `fromDomainModel_emptyPhoneNumber_mapsCorrectly`() {
        val entry =
            CallHistoryEntry(
                phoneNumber = "",
                timestamp = 1000L,
                autoReplyMessage = "Test message",
            )

        val entity = CallHistoryEntity.fromDomainModel(entry)

        assertEquals("", entity.phoneNumber)
    }

    @Test
    fun `fromDomainModel_longAutoReplyMessage_mapsCorrectly`() {
        val longMessage = "A".repeat(1000)
        val entry =
            CallHistoryEntry(
                phoneNumber = "+1234567890",
                timestamp = 1000L,
                autoReplyMessage = longMessage,
            )

        val entity = CallHistoryEntity.fromDomainModel(entry)

        assertEquals(longMessage, entity.autoReplyMessage)
    }
}
