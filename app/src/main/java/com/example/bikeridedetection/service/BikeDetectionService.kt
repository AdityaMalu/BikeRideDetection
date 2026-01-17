package com.example.bikeridedetection.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.bikeridedetection.R
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Foreground service for detecting bike activity using Activity Recognition API.
 */
@AndroidEntryPoint
class BikeDetectionService : Service() {
    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    private var transitionPendingIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        activityRecognitionClient = ActivityRecognition.getClient(this)
        requestActivityUpdates()
    }

    private fun requestActivityUpdates() {
        val transitions =
            listOf(
                ActivityTransition
                    .Builder()
                    .setActivityType(DetectedActivity.ON_BICYCLE)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition
                    .Builder()
                    .setActivityType(DetectedActivity.ON_BICYCLE)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build(),
            )

        val request = ActivityTransitionRequest(transitions)
        val intent = Intent(this, BikeTransitionReceiver::class.java)
        transitionPendingIntent =
            PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.e("Missing ACTIVITY_RECOGNITION permission")
            return
        }

        activityRecognitionClient
            .requestActivityTransitionUpdates(
                request,
                transitionPendingIntent!!,
            ).addOnSuccessListener {
                Timber.d("Bike activity updates registered")
            }.addOnFailureListener { e ->
                Timber.e(e, "Failed to register bike updates")
            }
    }

    private fun createNotification(): Notification =
        NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_detection_title))
            .setContentText(getString(R.string.notification_detection_text))
            .setSmallIcon(R.drawable.ic_bike)
            .setOngoing(true)
            .build()

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_detection_name),
                NotificationManager.IMPORTANCE_LOW,
            )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        transitionPendingIntent?.let { pendingIntent ->
            activityRecognitionClient.removeActivityTransitionUpdates(pendingIntent)
            Timber.d("Bike activity updates removed")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "BikeDetectionChannel"
        private const val NOTIFICATION_ID = 1
    }
}
