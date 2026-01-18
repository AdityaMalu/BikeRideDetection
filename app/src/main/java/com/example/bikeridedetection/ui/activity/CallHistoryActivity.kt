package com.example.bikeridedetection.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bikeridedetection.ui.screen.CallHistoryScreen
import com.example.bikeridedetection.ui.theme.BikeRideDetectionTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity for displaying call history.
 * Shows calls that were rejected during bike mode.
 */
@AndroidEntryPoint
class CallHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BikeRideDetectionTheme {
                CallHistoryScreen(
                    onNavigateBack = { finish() },
                )
            }
        }
    }

    companion object {
        /**
         * Creates an intent to launch CallHistoryActivity.
         *
         * @param context The context to create the intent from
         * @return The intent to launch the activity
         */
        fun createIntent(context: Context): Intent = Intent(context, CallHistoryActivity::class.java)
    }
}

