package com.example.bikeridedetection.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bikeridedetection.R;
import com.example.bikeridedetection.ui.MainActivity;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;

public class NotificationService extends Service {

    private static final String TAG = "BikeModeService";

    private static final String CHANNEL_ID = "bike_mode_status_v2";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "🚀 onCreate()");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "▶️ onStartCommand()");

        boolean enabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        Log.d(TAG, "🔎 areNotificationsEnabled=" + enabled);

        // 👉 Create an intent that broadcasts "BIKE_MODE_OFF"
        Intent offIntent = new Intent(this, MainActivity.class)
                .setAction("com.example.bikeridedetection.ACTION_BIKE_MODE_OFF")
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                offIntent,
                Build.VERSION.SDK_INT >= 31
                        ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentTitle("Bike Mode Active")
                .setContentText("Tap to turn off Bike Mode")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(contentIntent) // 🔥 now tapping triggers broadcast
                .build();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC);
                Log.d(TAG, "✅ startForeground(..., DATA_SYNC) called");
            } else {
                startForeground(NOTIFICATION_ID, notification);
                Log.d(TAG, "✅ startForeground() called (legacy)");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to startForeground: " + e.getMessage(), e);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "🛑 onDestroy()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "⚙️ createNotificationChannel()");
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Bike Mode",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows when Bike Mode is active");

            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.createNotificationChannel(channel);
                Log.d(TAG, "✅ Channel created: id=" + CHANNEL_ID
                        + ", importance=" + channel.getImportance());
            }
        }
    }
}
