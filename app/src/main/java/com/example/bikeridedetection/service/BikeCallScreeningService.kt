package com.example.bikeridedetection.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.example.bikeridedetection.domain.usecase.GetBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.SaveCallHistoryUseCase
import com.example.bikeridedetection.domain.usecase.SendAutoReplyUseCase
import com.example.bikeridedetection.util.ContactsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Call screening service that rejects calls when bike mode is enabled.
 * Sends an auto-reply SMS to the caller and saves the call to history.
 */
@AndroidEntryPoint
class BikeCallScreeningService : CallScreeningService() {
    @Inject
    lateinit var getBikeModeUseCase: GetBikeModeUseCase

    @Inject
    lateinit var sendAutoReplyUseCase: SendAutoReplyUseCase

    @Inject
    lateinit var saveCallHistoryUseCase: SaveCallHistoryUseCase

    @Inject
    lateinit var contactsHelper: ContactsHelper

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable, "Uncaught exception in BikeCallScreeningService coroutine")
        }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

    override fun onCreate() {
        super.onCreate()
        Timber.d("BikeCallScreeningService created")
    }

    override fun onScreenCall(callDetails: Call.Details) {
        Timber.d("onScreenCall called")

        // Check if dependencies are injected
        if (!::getBikeModeUseCase.isInitialized) {
            Timber.e("getBikeModeUseCase not initialized - Hilt injection may have failed")
            allowCall(callDetails)
            return
        }

        serviceScope.launch {
            try {
                val bikeMode = getBikeModeUseCase()
                val phoneNumber = extractPhoneNumber(callDetails.handle?.toString())

                Timber.d("Incoming call: $phoneNumber | BikeMode=${bikeMode.isEnabled}")

                if (bikeMode.isEnabled) {
                    rejectCall(callDetails)
                    phoneNumber?.let { number ->
                        // Use NonCancellable to ensure these operations complete
                        // even if the service is destroyed
                        withContext(NonCancellable) {
                            sendAutoReply(number)
                            saveCallHistory(number, bikeMode.autoReplyMessage)
                        }
                    }
                } else {
                    allowCall(callDetails)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in onScreenCall")
                // Allow the call if there's an error
                allowCall(callDetails)
            }
        }
    }

    private fun rejectCall(callDetails: Call.Details) {
        val response =
            CallResponse
                .Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
                .build()
        respondToCall(callDetails, response)
        Timber.d("Call rejected")
    }

    private fun allowCall(callDetails: Call.Details) {
        val response =
            CallResponse
                .Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .build()
        respondToCall(callDetails, response)
        Timber.d("Call allowed")
    }

    private suspend fun sendAutoReply(phoneNumber: String) {
        try {
            val result = sendAutoReplyUseCase(phoneNumber)
            Timber.d("Auto-reply result: $result")
        } catch (e: Exception) {
            Timber.e(e, "Failed to send auto-reply to $phoneNumber")
        }
    }

    private suspend fun saveCallHistory(
        phoneNumber: String,
        autoReplyMessage: String,
    ) {
        try {
            Timber.d("Saving call history for $phoneNumber")
            val isFromContact = contactsHelper.isPhoneNumberInContacts(phoneNumber)
            Timber.d("Is from contact: $isFromContact")
            val entryId = saveCallHistoryUseCase(phoneNumber, isFromContact, autoReplyMessage)
            Timber.d("Call history saved successfully with ID: $entryId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save call history for $phoneNumber")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't cancel immediately - let pending operations complete
        Timber.d("BikeCallScreeningService onDestroy called")
        serviceScope.cancel()
        Timber.d("BikeCallScreeningService destroyed")
    }

    companion object {
        private const val TEL_URI_PREFIX = "tel:"

        /**
         * Extracts the phone number from a tel: URI.
         *
         * @param input The tel: URI string
         * @return The extracted phone number, or null if invalid
         */
        fun extractPhoneNumber(input: String?): String? {
            if (input == null || !input.startsWith(TEL_URI_PREFIX)) {
                return null
            }
            return input.removePrefix(TEL_URI_PREFIX).replace("%2B", "+")
        }
    }
}
