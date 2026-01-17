package com.example.bikeridedetection.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Broadcast receiver for handling bike activity transitions.
 * Automatically enables/disables bike mode based on detected activity.
 */
@AndroidEntryPoint
class BikeTransitionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var bikeModeRepository: BikeModeRepository

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (!ActivityTransitionResult.hasResult(intent)) {
            return
        }

        val result = ActivityTransitionResult.extractResult(intent) ?: return
        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                result.transitionEvents.forEach { event ->
                    Timber.d(
                        "Detected transition: type=${event.activityType}, " +
                            "transition=${event.transitionType}",
                    )

                    val bikeModeOn = event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                    bikeModeRepository.setBikeModeEnabled(bikeModeOn)

                    Timber.d("Bike mode toggled: $bikeModeOn")

                    // Broadcast to other components
                    val broadcast =
                        Intent(ACTION_BIKE_MODE_CHANGED).apply {
                            putExtra(KEY_BIKE_MODE, bikeModeOn)
                            setPackage(context.packageName)
                        }
                    context.sendBroadcast(broadcast)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_BIKE_MODE_CHANGED = "com.example.bikeridedetection.BIKE_MODE_CHANGED"
        const val KEY_BIKE_MODE = "bike_mode"
    }
}
