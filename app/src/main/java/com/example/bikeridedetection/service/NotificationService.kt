package com.example.bikeridedetection.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.bikeridedetection.R
import com.example.bikeridedetection.ui.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Foreground service that shows a notification when bike mode is active.
 * Tapping the notification turns off bike mode.
 */
@AndroidEntryPoint
class NotificationService : Service() {
    override fun onCreate() {
        super.onCreate()
        Timber.d("NotificationService created")
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        Timber.d("NotificationService started")

        val notification = createNotification()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            Timber.d("Foreground service started successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to start foreground service")
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val offIntent =
            Intent(this, MainActivity::class.java).apply {
                action = MainActivity.ACTION_BIKE_MODE_OFF
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                offIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bike)
            .setContentTitle(getString(R.string.notification_active_title))
            .setContentText(getString(R.string.notification_active_text))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(contentIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_active_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.channel_active_description)
            }

        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
        Timber.d("Notification channel created: $CHANNEL_ID")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("NotificationService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "bike_mode_status_v2"
        private const val NOTIFICATION_ID = 1001
    }
}
