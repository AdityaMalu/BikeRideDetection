package com.example.bikeridedetection.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.bikeridedetection.data.datasource.BikeModeDataStore
import com.example.bikeridedetection.ui.screen.OnboardingScreen
import com.example.bikeridedetection.ui.theme.BikeRideDetectionTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity for the onboarding flow shown to first-time users.
 * Introduces app features and explains required permissions.
 */
@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    @Inject
    lateinit var bikeModeDataStore: BikeModeDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BikeRideDetectionTheme {
                OnboardingScreen(
                    onComplete = { completeOnboarding() },
                    onSkip = { completeOnboarding() },
                )
            }
        }
    }

    private fun completeOnboarding() {
        lifecycleScope.launch {
            bikeModeDataStore.setOnboardingCompleted(true)
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        finish()
    }
}
