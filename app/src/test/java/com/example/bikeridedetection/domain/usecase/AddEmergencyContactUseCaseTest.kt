package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@Suppress("MagicNumber")
class AddEmergencyContactUseCaseTest {
    private lateinit var repository: EmergencyContactRepository
    private lateinit var useCase: AddEmergencyContactUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddEmergencyContactUseCase(repository)
    }

    @Test
    fun `invoke_withContact_addsContactAndReturnsId`() =
        runTest {
            val contact =
                EmergencyContact(
                    phoneNumber = "+1234567890",
                    displayName = "Mom",
                )
            coEvery { repository.addContact(any()) } returns 1L

            val result = useCase(contact)

            assertEquals(1L, result)
            coVerify { repository.addContact(contact) }
        }

    @Test
    fun `invoke_withDetails_createsContactAndAdds`() =
        runTest {
            val contactSlot = slot<EmergencyContact>()
            coEvery { repository.addContact(capture(contactSlot)) } returns 2L

            val result = useCase("+1234567890", "Dad", "content://contacts/1")

            assertEquals(2L, result)
            assertEquals("+1234567890", contactSlot.captured.phoneNumber)
            assertEquals("Dad", contactSlot.captured.displayName)
            assertEquals("content://contacts/1", contactSlot.captured.contactUri)
        }

    @Test
    fun `invoke_withoutContactUri_createsContactWithNullUri`() =
        runTest {
            val contactSlot = slot<EmergencyContact>()
            coEvery { repository.addContact(capture(contactSlot)) } returns 3L

            val result = useCase("+1234567890", "Sister")

            assertEquals(3L, result)
            assertEquals(null, contactSlot.captured.contactUri)
        }
}
