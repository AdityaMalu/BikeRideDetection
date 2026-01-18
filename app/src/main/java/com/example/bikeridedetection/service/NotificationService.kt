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
import com.example.bikeridedetection.ui.activity.CallHistoryActivity
import com.example.bikeridedetection.ui.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Foreground service that shows a notification when bike mode is active.
 * Provides quick actions:
 * - Tap notification: Opens main activity
 * - End Ride: Turns off bike mode
 * - Allow Next Call: Allows the next incoming call through
 * - View Missed: Opens call history
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
        // Content intent - opens main activity
        val contentIntent = createContentIntent()

        // Action 1: End Ride (turns off bike mode)
        val endRideAction = createEndRideAction()

        // Action 2: Allow Next Call
        val allowNextCallAction = createAllowNextCallAction()

        // Action 3: View Missed Calls
        val viewMissedAction = createViewMissedAction()

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bike)
            .setContentTitle(getString(R.string.notification_active_title))
            .setContentText(getString(R.string.notification_active_text))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(contentIntent)
            .addAction(endRideAction)
            .addAction(allowNextCallAction)
            .addAction(viewMissedAction)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(getString(R.string.notification_active_expanded_text)),
            ).build()
    }

    private fun createContentIntent(): PendingIntent {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        return PendingIntent.getActivity(
            this,
            REQUEST_CODE_CONTENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createEndRideAction(): NotificationCompat.Action {
        val intent =
            Intent(this, NotificationActionReceiver::class.java).apply {
                action = ACTION_END_RIDE
            }
        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                REQUEST_CODE_END_RIDE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        return NotificationCompat
            .Action
            .Builder(
                R.drawable.ic_bike,
                getString(R.string.notification_action_end_ride),
                pendingIntent,
            ).build()
    }

    private fun createAllowNextCallAction(): NotificationCompat.Action {
        val intent =
            Intent(this, NotificationActionReceiver::class.java).apply {
                action = ACTION_ALLOW_NEXT_CALL
            }
        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                REQUEST_CODE_ALLOW_NEXT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        return NotificationCompat
            .Action
            .Builder(
                R.drawable.ic_phone_blocked,
                getString(R.string.notification_action_allow_next),
                pendingIntent,
            ).build()
    }

    private fun createViewMissedAction(): NotificationCompat.Action {
        val intent =
            Intent(this, CallHistoryActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                REQUEST_CODE_VIEW_MISSED,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        return NotificationCompat
            .Action
            .Builder(
                R.drawable.ic_call_history,
                getString(R.string.notification_action_view_missed),
                pendingIntent,
            ).build()
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

        // Request codes for PendingIntents
        private const val REQUEST_CODE_CONTENT = 0
        private const val REQUEST_CODE_END_RIDE = 1
        private const val REQUEST_CODE_ALLOW_NEXT = 2
        private const val REQUEST_CODE_VIEW_MISSED = 3

        // Actions for notification buttons
        const val ACTION_END_RIDE = "com.example.bikeridedetection.ACTION_END_RIDE"
        const val ACTION_ALLOW_NEXT_CALL = "com.example.bikeridedetection.ACTION_ALLOW_NEXT_CALL"
    }
}
