package com.example.bikeridedetection.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.bikeridedetection.R;
import com.example.bikeridedetection.ui.activity.MainActivity;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

/**
 * Foreground service that shows a notification when bike mode is active.
 * Tapping the notification turns off bike mode.
 */
@AndroidEntryPoint
public class NotificationService extends Service {

    private static final String CHANNEL_ID = "bike_mode_status_v2";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("NotificationService created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Timber.d("NotificationService started");

        Notification notification = createNotification();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                );
            } else {
                startForeground(NOTIFICATION_ID, notification);
            }
            Timber.d("Foreground service started successfully");
        } catch (Exception e) {
            Timber.e(e, "Failed to start foreground service");
        }

        return START_STICKY;
    }

    @NonNull
    private Notification createNotification() {
        Intent offIntent = new Intent(this, MainActivity.class);
        offIntent.setAction(MainActivity.ACTION_BIKE_MODE_OFF);
        offIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                offIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_active_title))
                .setContentText(getString(R.string.notification_active_text))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(contentIntent)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_active_name),
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription(getString(R.string.channel_active_description));

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
        Timber.d("Notification channel created: %s", CHANNEL_ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("NotificationService destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(@Nullable Intent intent) {
        return null;
    }
}

