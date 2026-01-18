package com.example.bikeridedetection.data.repository

import android.telephony.SmsManager
import com.example.bikeridedetection.domain.model.SmsResult
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SmsRepositoryImplTest {
    private lateinit var smsManager: SmsManager
    private lateinit var repository: SmsRepositoryImpl

    @Before
    fun setup() {
        smsManager = mockk()
        repository = SmsRepositoryImpl(smsManager)
    }

    @Test
    fun `sendSms_validPhoneNumber_returnsSent`() =
        runTest {
            val phoneNumber = "+1234567890"
            val message = "Test message"
            every {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            } just runs

            val result = repository.sendSms(phoneNumber, message)

            assertTrue(result is SmsResult.Sent)
            assertEquals(phoneNumber, (result as SmsResult.Sent).phoneNumber)
            verify {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            }
        }

    @Test
    fun `sendSms_blankPhoneNumber_returnsInvalidNumber`() =
        runTest {
            val result = repository.sendSms("", "Test message")

            assertTrue(result is SmsResult.InvalidNumber)
        }

    @Test
    fun `sendSms_whitespaceOnlyPhoneNumber_returnsInvalidNumber`() =
        runTest {
            val result = repository.sendSms("   ", "Test message")

            assertTrue(result is SmsResult.InvalidNumber)
        }

    @Test
    fun `sendSms_smsManagerThrowsException_returnsFailed`() =
        runTest {
            val phoneNumber = "+1234567890"
            val message = "Test message"
            val exception = RuntimeException("SMS sending failed")
            every {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            } throws exception

            val result = repository.sendSms(phoneNumber, message)

            assertTrue(result is SmsResult.Failed)
            assertEquals(phoneNumber, (result as SmsResult.Failed).phoneNumber)
            assertEquals(exception, result.error)
        }

    @Test
    fun `sendSms_securityException_returnsFailed`() =
        runTest {
            val phoneNumber = "+1234567890"
            val message = "Test message"
            val exception = SecurityException("No SMS permission")
            every {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            } throws exception

            val result = repository.sendSms(phoneNumber, message)

            assertTrue(result is SmsResult.Failed)
            assertTrue((result as SmsResult.Failed).error is SecurityException)
        }

    @Test
    fun `sendSms_emptyMessage_sendsSms`() =
        runTest {
            val phoneNumber = "+1234567890"
            val message = ""
            every {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            } just runs

            val result = repository.sendSms(phoneNumber, message)

            assertTrue(result is SmsResult.Sent)
        }

    @Test
    fun `sendSms_longMessage_sendsSms`() =
        runTest {
            val phoneNumber = "+1234567890"
            val message = "A".repeat(500)
            every {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            } just runs

            val result = repository.sendSms(phoneNumber, message)

            assertTrue(result is SmsResult.Sent)
        }

    @Test
    fun `sendSms_internationalNumber_sendsSms`() =
        runTest {
            val phoneNumber = "+44 20 7946 0958"
            val message = "Test message"
            every {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null,
                )
            } just runs

            val result = repository.sendSms(phoneNumber, message)

            assertTrue(result is SmsResult.Sent)
            assertEquals(phoneNumber, (result as SmsResult.Sent).phoneNumber)
        }
}
