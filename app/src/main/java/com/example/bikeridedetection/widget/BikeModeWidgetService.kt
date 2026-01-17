package com.example.bikeridedetection.widget

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.example.bikeridedetection.domain.usecase.ObserveBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.ToggleBikeModeUseCase
import com.example.bikeridedetection.service.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Service that handles widget toggle actions and observes bike mode state changes.
 * Uses Hilt for dependency injection to access the same use cases as the main app.
 */
@AndroidEntryPoint
class BikeModeWidgetService : Service() {

    @Inject
    lateinit var toggleBikeModeUseCase: ToggleBikeModeUseCase

    @Inject
    lateinit var observeBikeModeUseCase: ObserveBikeModeUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observeJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("BikeModeWidgetService onStartCommand: ${intent?.action}")

        when (intent?.action) {
            ACTION_TOGGLE -> {
                toggleBikeMode()
            }
            ACTION_START_OBSERVING -> {
                startObserving()
            }
            ACTION_STOP_OBSERVING -> {
                stopObserving()
                stopSelf()
            }
            else -> {
                // Default: start observing if not already
                if (observeJob == null || observeJob?.isActive != true) {
                    startObserving()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun toggleBikeMode() {
        serviceScope.launch {
            try {
                Timber.d("Toggling bike mode from widget")
                toggleBikeModeUseCase()
                Timber.d("Bike mode toggled successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle bike mode from widget")
            }
        }
    }

    private fun startObserving() {
        if (observeJob?.isActive == true) {
            Timber.d("Already observing bike mode state")
            return
        }

        Timber.d("Starting to observe bike mode state for widget")
        observeJob = observeBikeModeUseCase()
            .onEach { bikeMode ->
                Timber.d("Widget received bike mode update: isEnabled=${bikeMode.isEnabled}")

                // Update all widgets with animation
                val updateIntent = Intent(this, BikeModeWidgetProvider::class.java).apply {
                    action = BikeModeWidgetProvider.ACTION_UPDATE_WIDGET
                    putExtra(BikeModeWidgetProvider.EXTRA_IS_ENABLED, bikeMode.isEnabled)
                    putExtra(BikeModeWidgetProvider.EXTRA_ANIMATE, true)
                }
                sendBroadcast(updateIntent)

                // Update NotificationService to keep it in sync
                updateNotificationService(bikeMode.isEnabled)
            }
            .catch { error ->
                Timber.e(error, "Error observing bike mode state for widget")
            }
            .launchIn(serviceScope)
    }

    /**
     * Starts or stops the NotificationService based on bike mode state.
     * This ensures the notification stays in sync regardless of where the toggle originated.
     */
    private fun updateNotificationService(isEnabled: Boolean) {
        val notificationIntent = Intent(this, NotificationService::class.java)
        if (isEnabled) {
            Timber.d("Starting NotificationService from widget service")
            ContextCompat.startForegroundService(this, notificationIntent)
        } else {
            Timber.d("Stopping NotificationService from widget service")
            stopService(notificationIntent)
        }
    }

    private fun stopObserving() {
        Timber.d("Stopping bike mode observation for widget")
        observeJob?.cancel()
        observeJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("BikeModeWidgetService destroyed")
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_TOGGLE = "com.example.bikeridedetection.widget.ACTION_TOGGLE"
        const val ACTION_START_OBSERVING = "com.example.bikeridedetection.widget.ACTION_START_OBSERVING"
        const val ACTION_STOP_OBSERVING = "com.example.bikeridedetection.widget.ACTION_STOP_OBSERVING"
    }
}

