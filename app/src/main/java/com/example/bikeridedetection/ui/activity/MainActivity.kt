package com.example.bikeridedetection.ui.activity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bikeridedetection.R
import com.example.bikeridedetection.databinding.ActivityMainBinding
import com.example.bikeridedetection.service.BikeDetectionService
import com.example.bikeridedetection.service.NotificationService
import com.example.bikeridedetection.ui.viewmodel.MainViewModel
import com.example.bikeridedetection.util.PermissionManager
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
    lateinit var permissionManager: PermissionManager

    private var isUpdatingFromViewModel = false
    private var previousBikeModeState: Boolean? = null
    private var hasCheckedPermissions = false

    private val permissionFlowLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { _ ->
            Timber.d("Permission flow completed")
            permissionManager.logPermissionStatus()
            startBikeDetectionServiceIfPermitted()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("MainActivity created")

        setupUI()
        observeViewModel()
        handleIntentAction(intent)
    }

    override fun onResume() {
        super.onResume()
        // Check permissions on resume (in case user granted them in settings)
        if (!hasCheckedPermissions) {
            hasCheckedPermissions = true
            checkAndRequestPermissions()
        }
    }

    private fun setupUI() {
        setupToolbar()

        binding.switchBikeMode.setOnCheckedChangeListener { view, isChecked ->
            if (!isUpdatingFromViewModel) {
                // Provide haptic feedback for toggle
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                viewModel.setBikeModeEnabled(isChecked)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
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
        val shouldAnimate = previousBikeModeState != null && previousBikeModeState != isEnabled

        // Update switch state
        if (binding.switchBikeMode.isChecked != isEnabled) {
            binding.switchBikeMode.isChecked = isEnabled
        }

        // Update status text and styling
        binding.statusText.text = getString(if (isEnabled) STATUS_ON_RES else STATUS_OFF_RES)
        binding.statusText.setBackgroundResource(
            if (isEnabled) R.drawable.bg_status_active else R.drawable.bg_status_inactive,
        )
        binding.statusText.setTextAppearance(
            if (isEnabled) {
                R.style.TextAppearance_BikeRide_StatusActive
            } else {
                R.style.TextAppearance_BikeRide_StatusInactive
            },
        )

        // Update bike icon with animation
        val targetColor =
            ContextCompat.getColor(
                this,
                if (isEnabled) R.color.status_active else R.color.status_inactive,
            )

        if (shouldAnimate) {
            animateBikeIcon(isEnabled, targetColor)
            animateHeroCard(isEnabled)
        } else {
            binding.bikeIcon.setColorFilter(targetColor)
        }

        // Update feature card status text
        val featureStatusRes =
            if (isEnabled) R.string.feature_status_active else R.string.feature_status_ready
        val featureStatus = getString(featureStatusRes)
        binding.callBlockingStatus.text = featureStatus
        binding.autoReplyStatus.text = featureStatus

        // Update accessibility content description
        val statusDescription =
            if (isEnabled) R.string.cd_status_active else R.string.cd_status_inactive
        binding.switchBikeMode.contentDescription =
            getString(R.string.cd_bike_mode_switch) + " " + getString(statusDescription)

        // Announce state change for screen readers
        if (shouldAnimate) {
            val announcementRes =
                if (isEnabled) {
                    R.string.announce_bike_mode_activated
                } else {
                    R.string.announce_bike_mode_deactivated
                }
            val announcement = getString(announcementRes)
            @Suppress("DEPRECATION")
            binding.root.announceForAccessibility(announcement)
        }

        previousBikeModeState = isEnabled
        isUpdatingFromViewModel = false
    }

    private fun animateBikeIcon(
        isEnabled: Boolean,
        targetColor: Int,
    ) {
        // Scale animation with bounce effect
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)

        ObjectAnimator.ofPropertyValuesHolder(binding.bikeIcon, scaleX, scaleY).apply {
            duration = ANIMATION_DURATION
            interpolator = OvershootInterpolator()
            start()
        }

        // Color transition animation
        val fromColor =
            ContextCompat.getColor(
                this,
                if (isEnabled) R.color.status_inactive else R.color.status_active,
            )

        ObjectAnimator
            .ofObject(
                binding.bikeIcon,
                "colorFilter",
                ArgbEvaluator(),
                fromColor,
                targetColor,
            ).apply {
                duration = ANIMATION_DURATION
                start()
            }

        // Rotation animation when activating
        if (isEnabled) {
            ObjectAnimator.ofFloat(binding.bikeIcon, View.ROTATION, 0f, 360f).apply {
                duration = ANIMATION_DURATION * 2
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }

    private fun animateHeroCard(isEnabled: Boolean) {
        // Subtle pulse animation on the hero card
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.02f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.02f, 1f)

        ObjectAnimator.ofPropertyValuesHolder(binding.heroCard, scaleX, scaleY).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
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

    private fun checkAndRequestPermissions() {
        if (!permissionManager.areAllPermissionsGranted()) {
            Timber.d("Not all permissions granted, launching permission flow")
            val intent = PermissionFlowActivity.createIntent(this)
            permissionFlowLauncher.launch(intent)
        } else {
            Timber.d("All permissions already granted")
            startBikeDetectionServiceIfPermitted()
        }
    }

    private fun startBikeDetectionServiceIfPermitted() {
        // Check if we have at least location permission to start the service
        val hasLocationPermission =
            permissionManager
                .getPermissionSteps()
                .filterIsInstance<com.example.bikeridedetection.util.PermissionStep.RuntimePermission>()
                .filter { it.permissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION) }
                .all { permissionManager.isStepGranted(it) }

        if (hasLocationPermission) {
            startBikeDetectionService()
        } else {
            Timber.w("Location permission not granted, cannot start bike detection service")
            showPermissionDeniedMessage()
        }
    }

    private fun startBikeDetectionService() {
        val serviceIntent = Intent(this, BikeDetectionService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun showPermissionDeniedMessage() {
        Snackbar
            .make(
                binding.root,
                R.string.permission_location_required,
                Snackbar.LENGTH_LONG,
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
        private val STATUS_ON_RES = R.string.status_on
        private val STATUS_OFF_RES = R.string.status_off
        private const val ANIMATION_DURATION = 300L
    }
}
