package com.example.bikeridedetection.data.repository

import android.telephony.SmsManager
import com.example.bikeridedetection.domain.model.SmsResult
import com.example.bikeridedetection.domain.repository.SmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SmsRepository] using Android's SmsManager.
 */
@Singleton
class SmsRepositoryImpl
    @Inject
    constructor(
        private val smsManager: SmsManager,
    ) : SmsRepository {
        override suspend fun sendSms(
            phoneNumber: String,
            message: String,
        ): SmsResult {
            return withContext(Dispatchers.IO) {
                if (phoneNumber.isBlank()) {
                    Timber.w("Cannot send SMS: invalid phone number")
                    return@withContext SmsResult.InvalidNumber
                }

                try {
                    smsManager.sendTextMessage(
                        phoneNumber,
                        null,
                        message,
                        null,
                        null,
                    )
                    Timber.d("SMS sent successfully to $phoneNumber")
                    SmsResult.Sent(phoneNumber)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to send SMS to $phoneNumber")
                    SmsResult.Failed(phoneNumber, e)
                }
            }
        }
    }
