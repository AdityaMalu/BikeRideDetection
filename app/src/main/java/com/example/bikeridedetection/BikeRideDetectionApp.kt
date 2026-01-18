package com.example.bikeridedetection

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.bikeridedetection.widget.BikeModeWidgetProvider
import com.example.bikeridedetection.widget.BikeModeWidgetService
import com.example.bikeridedetection.worker.CallHistoryCleanupWorker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for BikeRideDetection app.
 * Initializes Hilt dependency injection, Timber logging, Firebase services, and WorkManager.
 */
@HiltAndroidApp
class BikeRideDetectionApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        initializeFirebase()
        initializeWidgetService()
        scheduleCallHistoryCleanup()
    }

    private fun scheduleCallHistoryCleanup() {
        CallHistoryCleanupWorker.schedule(this)
    }

    private fun initializeWidgetService() {
        // Start widget service if there are active widgets
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, BikeModeWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        if (appWidgetIds.isNotEmpty()) {
            Timber.d("Found ${appWidgetIds.size} active widgets, starting widget service")
            val serviceIntent =
                Intent(this, BikeModeWidgetService::class.java).apply {
                    action = BikeModeWidgetService.ACTION_START_OBSERVING
                }
            startService(serviceIntent)
        }
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In release builds, plant a tree that reports to Crashlytics
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun initializeFirebase() {
        // Configure Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            // Disable collection in debug builds for cleaner crash reports
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

            // Set custom keys for better crash context
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
            setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        }

        // Configure Performance Monitoring
        FirebasePerformance.getInstance().apply {
            // Enable/disable based on build type
            isPerformanceCollectionEnabled = !BuildConfig.DEBUG
        }
    }

    /**
     * Custom Timber tree that logs to Firebase Crashlytics in release builds.
     * Only logs warnings and errors to avoid cluttering crash reports.
     */
    private class CrashlyticsTree : Timber.Tree() {
        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?,
        ) {
            if (priority < Log.WARN) {
                return // Only log warnings and errors
            }

            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("${tag ?: "BikeRide"}: $message")

            if (t != null) {
                crashlytics.recordException(t)
            }
        }
    }
}
