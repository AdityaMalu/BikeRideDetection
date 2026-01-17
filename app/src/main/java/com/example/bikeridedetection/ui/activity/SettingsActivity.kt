package com.example.bikeridedetection.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bikeridedetection.ui.screen.SettingsScreen
import com.example.bikeridedetection.ui.theme.BikeRideDetectionTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity for app settings.
 * Uses Jetpack Compose for the UI.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BikeRideDetectionTheme {
                SettingsScreen(
                    onNavigateBack = { finish() },
                )
            }
        }
    }
}
