package com.example.bikeridedetection.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.bikeridedetection.R
import com.example.bikeridedetection.domain.usecase.AllowNextCallUseCase
import com.example.bikeridedetection.domain.usecase.SetBikeModeEnabledUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Broadcast receiver for handling notification action button clicks.
 */
@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var setBikeModeEnabledUseCase: SetBikeModeEnabledUseCase

    @Inject
    lateinit var allowNextCallUseCase: AllowNextCallUseCase

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        Timber.d("NotificationActionReceiver received action: ${intent.action}")

        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                when (intent.action) {
                    NotificationService.ACTION_END_RIDE -> handleEndRide(context)
                    NotificationService.ACTION_ALLOW_NEXT_CALL -> handleAllowNextCall(context)
                    else -> Timber.w("Unknown action: ${intent.action}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling notification action")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleEndRide(context: Context) {
        Timber.d("Handling END_RIDE action")
        setBikeModeEnabledUseCase(false)

        // Stop the notification service
        val serviceIntent = Intent(context, NotificationService::class.java)
        context.stopService(serviceIntent)

        // Show confirmation toast
        showToast(context, R.string.notification_toast_ride_ended)
    }

    private suspend fun handleAllowNextCall(context: Context) {
        Timber.d("Handling ALLOW_NEXT_CALL action")
        allowNextCallUseCase.enable()

        // Show confirmation toast
        showToast(context, R.string.notification_toast_next_call_allowed)
    }

    private fun showToast(
        context: Context,
        messageResId: Int,
    ) {
        // Toast must be shown on main thread
        android.os.Handler(context.mainLooper).post {
            Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
        }
    }
}
