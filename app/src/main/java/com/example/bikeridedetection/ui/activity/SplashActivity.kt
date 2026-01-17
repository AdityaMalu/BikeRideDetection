package com.example.bikeridedetection.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Splash screen activity that determines whether to show onboarding or main screen.
 * Uses the Android 12+ splash screen API for a smooth launch experience.
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    @Inject
    lateinit var bikeModeDataStore: BikeModeDataStore

    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep splash screen visible until we determine navigation
        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            val onboardingCompleted = bikeModeDataStore.isOnboardingCompleted()
            navigateToNextScreen(onboardingCompleted)
            isReady = true
        }
    }

    private fun navigateToNextScreen(onboardingCompleted: Boolean) {
        val targetActivity =
            if (onboardingCompleted) {
                MainActivity::class.java
            } else {
                OnboardingActivity::class.java
            }

        val intent =
            Intent(this, targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        finish()
    }
}
