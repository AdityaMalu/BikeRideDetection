package com.example.bikeridedetection.domain.usecase

import app.cash.turbine.test
import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetEmergencyContactsUseCaseTest {
    private lateinit var repository: EmergencyContactRepository
    private lateinit var useCase: GetEmergencyContactsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetEmergencyContactsUseCase(repository)
    }

    @Test
    fun `invoke_emptyList_returnsEmptyFlow`() =
        runTest {
            every { repository.getAllContacts() } returns flowOf(emptyList())

            useCase().test {
                assertEquals(emptyList<EmergencyContact>(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `invoke_withContacts_returnsContactsFlow`() =
        runTest {
            val contacts =
                listOf(
                    EmergencyContact(id = 1, phoneNumber = "+1234567890", displayName = "Mom"),
                    EmergencyContact(id = 2, phoneNumber = "+0987654321", displayName = "Dad"),
                )
            every { repository.getAllContacts() } returns flowOf(contacts)

            useCase().test {
                val result = awaitItem()
                assertEquals(2, result.size)
                assertEquals("Mom", result[0].displayName)
                assertEquals("Dad", result[1].displayName)
                awaitComplete()
            }
        }
}
