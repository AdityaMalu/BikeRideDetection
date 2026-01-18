package com.example.bikeridedetection.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.bikeridedetection.R;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

/**
 * Foreground service for detecting bike activity using Activity Recognition API.
 */
@AndroidEntryPoint
public class BikeDetectionService extends Service {

    private static final String CHANNEL_ID = "BikeDetectionChannel";
    private static final int NOTIFICATION_ID = 1;

    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent transitionPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        activityRecognitionClient = ActivityRecognition.getClient(this);
        requestActivityUpdates();
    }

    private void requestActivityUpdates() {
        List<ActivityTransition> transitions = Arrays.asList(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build(),
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()
        );

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);
        Intent intent = new Intent(this, BikeTransitionReceiver.class);
        transitionPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Missing ACTIVITY_RECOGNITION permission");
            return;
        }

        activityRecognitionClient.requestActivityTransitionUpdates(request, transitionPendingIntent)
                .addOnSuccessListener(unused -> Timber.d("Bike activity updates registered"))
                .addOnFailureListener(e -> Timber.e(e, "Failed to register bike updates"));
    }

    @NonNull
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_detection_title))
                .setContentText(getString(R.string.notification_detection_text))
                .setSmallIcon(R.drawable.ic_bike)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_detection_name),
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (transitionPendingIntent != null) {
            activityRecognitionClient.removeActivityTransitionUpdates(transitionPendingIntent);
            Timber.d("Bike activity updates removed");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(@Nullable Intent intent) {
        return null;
    }
}

