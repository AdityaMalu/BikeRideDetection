package com.example.bikeridedetection.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.example.bikeridedetection.domain.usecase.GetBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.SendAutoReplyUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Call screening service that rejects calls when bike mode is enabled.
 * Sends an auto-reply SMS to the caller.
 */
@AndroidEntryPoint
class BikeCallScreeningService : CallScreeningService() {
    @Inject
    lateinit var getBikeModeUseCase: GetBikeModeUseCase

    @Inject
    lateinit var sendAutoReplyUseCase: SendAutoReplyUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        Timber.d("BikeCallScreeningService created")
    }

    override fun onScreenCall(callDetails: Call.Details) {
        serviceScope.launch {
            val bikeMode = getBikeModeUseCase()
            val phoneNumber = extractPhoneNumber(callDetails.handle?.toString())

            Timber.d("Incoming call: $phoneNumber | BikeMode=${bikeMode.isEnabled}")

            if (bikeMode.isEnabled) {
                rejectCall(callDetails)
                phoneNumber?.let { sendAutoReply(it) }
            } else {
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
        val result = sendAutoReplyUseCase(phoneNumber)
        Timber.d("Auto-reply result: $result")
    }

    override fun onDestroy() {
        super.onDestroy()
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
