package com.example.bikeridedetection.data.repository

import app.cash.turbine.test
import com.example.bikeridedetection.data.local.dao.EmergencyContactDao
import com.example.bikeridedetection.data.local.entity.EmergencyContactEntity
import com.example.bikeridedetection.domain.model.EmergencyContact
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("MagicNumber")
class EmergencyContactRepositoryImplTest {
    private lateinit var emergencyContactDao: EmergencyContactDao
    private lateinit var repository: EmergencyContactRepositoryImpl

    @Before
    fun setup() {
        emergencyContactDao = mockk()
        repository = EmergencyContactRepositoryImpl(emergencyContactDao)
    }

    @Test
    fun `addContact_validContact_insertsAndReturnsId`() =
        runTest {
            val contact =
                EmergencyContact(
                    id = 0,
                    phoneNumber = "+1234567890",
                    displayName = "Mom",
                )
            val entitySlot = slot<EmergencyContactEntity>()
            coEvery { emergencyContactDao.insert(capture(entitySlot)) } returns 1L

            val result = repository.addContact(contact)

            assertEquals(1L, result)
            assertEquals(contact.phoneNumber, entitySlot.captured.phoneNumber)
            assertEquals(contact.displayName, entitySlot.captured.displayName)
        }

    @Test
    fun `getAllContacts_emptyList_returnsEmptyFlow`() =
        runTest {
            every { emergencyContactDao.getAllContacts() } returns flowOf(emptyList())

            repository.getAllContacts().test {
                assertEquals(emptyList<EmergencyContact>(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `getAllContacts_withContacts_returnsMappedDomainModels`() =
        runTest {
            val entities =
                listOf(
                    EmergencyContactEntity(
                        id = 1,
                        phoneNumber = "+1234567890",
                        displayName = "Mom",
                    ),
                    EmergencyContactEntity(
                        id = 2,
                        phoneNumber = "+0987654321",
                        displayName = "Dad",
                    ),
                )
            every { emergencyContactDao.getAllContacts() } returns flowOf(entities)

            repository.getAllContacts().test {
                val result = awaitItem()
                assertEquals(2, result.size)
                assertEquals("Mom", result[0].displayName)
                assertEquals("Dad", result[1].displayName)
                awaitComplete()
            }
        }

    @Test
    fun `isEmergencyContact_existingContact_returnsTrue`() =
        runTest {
            coEvery { emergencyContactDao.isEmergencyContact(any()) } returns true

            val result = repository.isEmergencyContact("+1234567890")

            assertTrue(result)
        }

    @Test
    fun `isEmergencyContact_nonExistingContact_returnsFalse`() =
        runTest {
            coEvery { emergencyContactDao.isEmergencyContact(any()) } returns false

            val result = repository.isEmergencyContact("+1234567890")

            assertFalse(result)
        }

    @Test
    fun `isEmergencyContact_blankNumber_returnsFalse`() =
        runTest {
            val result = repository.isEmergencyContact("")

            assertFalse(result)
        }

    @Test
    fun `removeContact_withValidId_deletesById`() =
        runTest {
            val contact =
                EmergencyContact(
                    id = 5,
                    phoneNumber = "+1234567890",
                    displayName = "Mom",
                )
            coEvery { emergencyContactDao.deleteById(5L) } returns Unit

            repository.removeContact(contact)

            coVerify(exactly = 1) { emergencyContactDao.deleteById(5L) }
            coVerify(exactly = 0) { emergencyContactDao.delete(any()) }
        }

    @Test
    fun `removeContact_withZeroId_fallsBackToEntityDeletion`() =
        runTest {
            val contact =
                EmergencyContact(
                    id = 0,
                    phoneNumber = "+1234567890",
                    displayName = "Mom",
                )
            coEvery { emergencyContactDao.delete(any()) } returns Unit

            repository.removeContact(contact)

            coVerify(exactly = 0) { emergencyContactDao.deleteById(any()) }
            coVerify(exactly = 1) { emergencyContactDao.delete(any()) }
        }

    @Test
    fun `removeContactById_callsDao`() =
        runTest {
            coEvery { emergencyContactDao.deleteById(1L) } returns Unit

            repository.removeContactById(1L)

            coVerify(exactly = 1) { emergencyContactDao.deleteById(1L) }
        }

    @Test
    fun `getContactByPhoneNumber_existingContact_returnsContact`() =
        runTest {
            val entity =
                EmergencyContactEntity(
                    id = 1,
                    phoneNumber = "+1234567890",
                    displayName = "Mom",
                )
            coEvery { emergencyContactDao.getContactByPhoneNumber(any()) } returns entity

            val result = repository.getContactByPhoneNumber("+1234567890")

            assertEquals("Mom", result?.displayName)
        }

    @Test
    fun `getContactByPhoneNumber_nonExistingContact_returnsNull`() =
        runTest {
            coEvery { emergencyContactDao.getContactByPhoneNumber(any()) } returns null

            val result = repository.getContactByPhoneNumber("+1234567890")

            assertNull(result)
        }

    @Test
    fun `deleteAll_callsDao`() =
        runTest {
            coEvery { emergencyContactDao.deleteAll() } returns Unit

            repository.deleteAll()

            coVerify(exactly = 1) { emergencyContactDao.deleteAll() }
        }
}
