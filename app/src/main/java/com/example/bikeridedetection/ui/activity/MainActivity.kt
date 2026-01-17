package com.example.bikeridedetection.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bikeridedetection.databinding.ActivityMainBinding
import com.example.bikeridedetection.service.BikeDetectionService
import com.example.bikeridedetection.service.NotificationService
import com.example.bikeridedetection.ui.viewmodel.MainViewModel
import com.example.bikeridedetection.util.PermissionHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Main activity for the BikeRideDetection app.
 * Handles UI interactions and permission requests.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var permissionHelper: PermissionHelper

    private var isUpdatingFromViewModel = false

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Timber.d("Location permissions granted")
            startBikeDetectionService()
        } else {
            Timber.w("Location permissions denied")
            showPermissionDeniedMessage()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.d("Notification permission granted")
        } else {
            Timber.w("Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("MainActivity created")

        setupUI()
        observeViewModel()
        requestPermissions()
        handleIntentAction(intent)
    }

    private fun setupUI() {
        binding.switchBikeMode.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromViewModel) {
                viewModel.setBikeModeEnabled(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state.bikeMode.isEnabled)
                    handleBikeModeChange(state.bikeMode.isEnabled)
                    state.errorMessage?.let { showError(it) }
                }
            }
        }
    }

    private fun updateUI(isEnabled: Boolean) {
        isUpdatingFromViewModel = true
        if (binding.switchBikeMode.isChecked != isEnabled) {
            binding.switchBikeMode.isChecked = isEnabled
        }
        binding.statusText.text = getString(
            if (isEnabled) STATUS_ON_RES else STATUS_OFF_RES
        )
        isUpdatingFromViewModel = false
    }

    private fun handleBikeModeChange(isEnabled: Boolean) {
        val serviceIntent = Intent(this, NotificationService::class.java)
        if (isEnabled) {
            Timber.d("Starting NotificationService")
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            Timber.d("Stopping NotificationService")
            stopService(serviceIntent)
        }
    }

    private fun requestPermissions() {
        requestNotificationPermission()
        requestLocationPermissions()
        permissionHelper.requestSmsPermission(this)
        permissionHelper.requestPhoneAndContactsPermissions(this)
        permissionHelper.requestCallScreeningRole(this)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            startBikeDetectionService()
        } else {
            locationPermissionLauncher.launch(permissions)
        }
    }

    private fun startBikeDetectionService() {
        val serviceIntent = Intent(this, BikeDetectionService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun showPermissionDeniedMessage() {
        Snackbar.make(
            binding.root,
            "Location permission is required for bike detection",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        viewModel.clearError()
    }

    private fun handleIntentAction(intent: Intent?) {
        if (intent?.action == ACTION_BIKE_MODE_OFF) {
            Timber.d("Notification clicked - Turning OFF Bike Mode")
            viewModel.setBikeModeEnabled(false)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.d("onNewIntent() called with action=${intent.action}")
        handleIntentAction(intent)
    }

    companion object {
        const val ACTION_BIKE_MODE_OFF = "com.example.bikeridedetection.ACTION_BIKE_MODE_OFF"
        private val STATUS_ON_RES = com.example.bikeridedetection.R.string.status_on
        private val STATUS_OFF_RES = com.example.bikeridedetection.R.string.status_off
    }
}

