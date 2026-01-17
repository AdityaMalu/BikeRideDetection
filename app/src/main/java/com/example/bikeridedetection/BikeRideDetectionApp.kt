package com.example.bikeridedetection

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for BikeRideDetection app.
 * Initializes Hilt dependency injection, Timber logging, and Firebase services.
 */
@HiltAndroidApp
class BikeRideDetectionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        initializeFirebase()
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
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
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

