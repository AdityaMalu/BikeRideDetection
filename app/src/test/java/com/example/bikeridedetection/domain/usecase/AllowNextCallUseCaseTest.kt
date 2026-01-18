package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AllowNextCallUseCaseTest {
    private lateinit var bikeModeDataStore: BikeModeDataStore
    private lateinit var useCase: AllowNextCallUseCase

    @Before
    fun setup() {
        bikeModeDataStore = mockk(relaxed = true)
        useCase = AllowNextCallUseCase(bikeModeDataStore)
    }

    @Test
    fun `enable_setsAllowNextCallTrue`() =
        runTest {
            useCase.enable()

            coVerify { bikeModeDataStore.setAllowNextCall(true) }
        }

    @Test
    fun `disable_setsAllowNextCallFalse`() =
        runTest {
            useCase.disable()

            coVerify { bikeModeDataStore.setAllowNextCall(false) }
        }

    @Test
    fun `consumeIfEnabled_whenEnabled_returnsTrue`() =
        runTest {
            coEvery { bikeModeDataStore.consumeAllowNextCall() } returns true

            val result = useCase.consumeIfEnabled()

            assertTrue(result)
        }

    @Test
    fun `consumeIfEnabled_whenDisabled_returnsFalse`() =
        runTest {
            coEvery { bikeModeDataStore.consumeAllowNextCall() } returns false

            val result = useCase.consumeIfEnabled()

            assertFalse(result)
        }

    @Test
    fun `isEnabled_returnsDataStoreValue`() =
        runTest {
            coEvery { bikeModeDataStore.shouldAllowNextCall() } returns true

            val result = useCase.isEnabled()

            assertTrue(result)
        }
}
