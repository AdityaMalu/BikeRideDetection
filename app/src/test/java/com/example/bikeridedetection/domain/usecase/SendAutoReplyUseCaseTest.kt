package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.model.SmsResult
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import com.example.bikeridedetection.domain.repository.SmsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SendAutoReplyUseCaseTest {

    private lateinit var bikeModeRepository: BikeModeRepository
    private lateinit var smsRepository: SmsRepository
    private lateinit var useCase: SendAutoReplyUseCase

    @Before
    fun setup() {
        bikeModeRepository = mockk()
        smsRepository = mockk()
        useCase = SendAutoReplyUseCase(bikeModeRepository, smsRepository)
    }

    @Test
    fun `should send SMS with auto reply message`() = runTest {
        val phoneNumber = "+1234567890"
        val autoReplyMessage = "I'm riding my bike right now."
        val bikeMode = BikeMode(isEnabled = true, autoReplyMessage = autoReplyMessage)

        coEvery { bikeModeRepository.getBikeMode() } returns bikeMode
        coEvery { smsRepository.sendSms(phoneNumber, autoReplyMessage) } returns
            SmsResult.Sent(phoneNumber)

        val result = useCase(phoneNumber)

        assertEquals(SmsResult.Sent(phoneNumber), result)
        coVerify { smsRepository.sendSms(phoneNumber, autoReplyMessage) }
    }

    @Test
    fun `should return failed result on SMS error`() = runTest {
        val phoneNumber = "+1234567890"
        val autoReplyMessage = "I'm riding my bike right now."
        val bikeMode = BikeMode(isEnabled = true, autoReplyMessage = autoReplyMessage)
        val error = RuntimeException("SMS failed")

        coEvery { bikeModeRepository.getBikeMode() } returns bikeMode
        coEvery { smsRepository.sendSms(phoneNumber, autoReplyMessage) } returns
            SmsResult.Failed(phoneNumber, error)

        val result = useCase(phoneNumber)

        assertEquals(SmsResult.Failed(phoneNumber, error), result)
    }

    @Test
    fun `should return invalid number for blank phone number`() = runTest {
        val phoneNumber = ""
        val autoReplyMessage = "I'm riding my bike right now."
        val bikeMode = BikeMode(isEnabled = true, autoReplyMessage = autoReplyMessage)

        coEvery { bikeModeRepository.getBikeMode() } returns bikeMode
        coEvery { smsRepository.sendSms(phoneNumber, autoReplyMessage) } returns
            SmsResult.InvalidNumber

        val result = useCase(phoneNumber)

        assertEquals(SmsResult.InvalidNumber, result)
    }
}

