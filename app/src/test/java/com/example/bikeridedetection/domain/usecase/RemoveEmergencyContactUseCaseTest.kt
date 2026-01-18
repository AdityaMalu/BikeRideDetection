package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RemoveEmergencyContactUseCaseTest {
    private lateinit var repository: EmergencyContactRepository
    private lateinit var useCase: RemoveEmergencyContactUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = RemoveEmergencyContactUseCase(repository)
    }

    @Test
    fun `invoke_withContact_removesContact`() =
        runTest {
            val contact =
                EmergencyContact(
                    id = 1,
                    phoneNumber = "+1234567890",
                    displayName = "Mom",
                )
            coEvery { repository.removeContact(contact) } returns Unit

            useCase(contact)

            coVerify { repository.removeContact(contact) }
        }

    @Test
    fun `byId_removesContactById`() =
        runTest {
            coEvery { repository.removeContactById(1L) } returns Unit

            useCase.byId(1L)

            coVerify { repository.removeContactById(1L) }
        }
}
