package com.example.bikeridedetection.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bikeridedetection.ui.screen.EmergencyContactsScreen
import com.example.bikeridedetection.ui.theme.BikeRideDetectionTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity for managing emergency contacts.
 * Emergency contacts can bypass call blocking during bike mode.
 */
@AndroidEntryPoint
class EmergencyContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BikeRideDetectionTheme {
                EmergencyContactsScreen(
                    onNavigateBack = { finish() },
                )
            }
        }
    }

    companion object {
        /**
         * Creates an intent to launch EmergencyContactsActivity.
         *
         * @param context The context to create the intent from
         * @return The intent to launch the activity
         */
        fun createIntent(context: Context): Intent = Intent(context, EmergencyContactsActivity::class.java)
    }
}
