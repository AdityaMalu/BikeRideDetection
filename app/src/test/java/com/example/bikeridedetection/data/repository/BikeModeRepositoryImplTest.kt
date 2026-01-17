package com.example.bikeridedetection.data.repository

import app.cash.turbine.test
import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.domain.model.BikeMode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BikeModeRepositoryImplTest {

    private lateinit var dataStore: BikeModeDataStore
    private lateinit var repository: BikeModeRepositoryImpl

    @Before
    fun setup() {
        dataStore = mockk()
        repository = BikeModeRepositoryImpl(dataStore)
    }

    @Test
    fun `observeBikeMode should return flow from data store`() = runTest {
        val bikeMode = BikeMode(isEnabled = true, autoReplyMessage = "Test")
        every { dataStore.observeBikeMode() } returns flowOf(bikeMode)

        repository.observeBikeMode().test {
            assertEquals(bikeMode, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getBikeMode should return bike mode from data store`() = runTest {
        val bikeMode = BikeMode(isEnabled = true, autoReplyMessage = "Test")
        coEvery { dataStore.getBikeMode() } returns bikeMode

        val result = repository.getBikeMode()

        assertEquals(bikeMode, result)
    }

    @Test
    fun `setBikeModeEnabled should call data store`() = runTest {
        coEvery { dataStore.setBikeModeEnabled(true) } returns Unit

        repository.setBikeModeEnabled(true)

        coVerify { dataStore.setBikeModeEnabled(true) }
    }

    @Test
    fun `setAutoReplyMessage should call data store`() = runTest {
        val message = "Custom message"
        coEvery { dataStore.setAutoReplyMessage(message) } returns Unit

        repository.setAutoReplyMessage(message)

        coVerify { dataStore.setAutoReplyMessage(message) }
    }
}

