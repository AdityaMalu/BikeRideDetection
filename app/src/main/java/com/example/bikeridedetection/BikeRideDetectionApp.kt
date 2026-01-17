package com.example.bikeridedetection

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for BikeRideDetection app.
 * Initializes Hilt dependency injection and Timber logging.
 */
@HiltAndroidApp
class BikeRideDetectionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // In release builds, you could plant a crash reporting tree
    }
}

