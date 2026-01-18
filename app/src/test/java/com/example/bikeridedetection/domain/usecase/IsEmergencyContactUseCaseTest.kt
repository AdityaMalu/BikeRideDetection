package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.domain.repository.EmergencyContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsEmergencyContactUseCaseTest {
    private lateinit var repository: EmergencyContactRepository
    private lateinit var bikeModeDataStore: BikeModeDataStore
    private lateinit var useCase: IsEmergencyContactUseCase

    @Before
    fun setup() {
        repository = mockk()
        bikeModeDataStore = mockk()
        useCase = IsEmergencyContactUseCase(repository, bikeModeDataStore)
    }

    @Test
    fun `invoke_featureDisabled_returnsFalse`() =
        runTest {
            coEvery { bikeModeDataStore.isEmergencyContactsEnabled() } returns false

            val result = useCase("+1234567890")

            assertFalse(result)
            coVerify(exactly = 0) { repository.isEmergencyContact(any()) }
        }

    @Test
    fun `invoke_featureEnabled_contactExists_returnsTrue`() =
        runTest {
            coEvery { bikeModeDataStore.isEmergencyContactsEnabled() } returns true
            coEvery { repository.isEmergencyContact("+1234567890") } returns true

            val result = useCase("+1234567890")

            assertTrue(result)
        }

    @Test
    fun `invoke_featureEnabled_contactNotExists_returnsFalse`() =
        runTest {
            coEvery { bikeModeDataStore.isEmergencyContactsEnabled() } returns true
            coEvery { repository.isEmergencyContact("+1234567890") } returns false

            val result = useCase("+1234567890")

            assertFalse(result)
        }
}
