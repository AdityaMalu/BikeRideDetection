package com.example.bikeridedetection.util

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents a permission step in the sequential permission flow.
 */
sealed class PermissionStep {
    data class RuntimePermission(
        val permissions: List<String>,
        val rationaleTitle: String,
        val rationaleMessage: String,
    ) : PermissionStep()

    data class RoleRequest(
        val role: String,
        val rationaleTitle: String,
        val rationaleMessage: String,
    ) : PermissionStep()
}

/**
 * Manages sequential permission requests for the app.
 * Ensures permissions are requested one at a time with proper rationale.
 */
@Singleton
class PermissionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Returns the ordered list of permission steps to request.
         */
        fun getPermissionSteps(): List<PermissionStep> =
            buildList {
                add(createLocationPermissionStep())
                addNotificationPermissionStepIfNeeded(this)
                add(createSmsPermissionStep())
                add(createPhoneContactsPermissionStep())
                addCallScreeningRoleIfNeeded(this)
            }

        private fun createLocationPermissionStep() =
            PermissionStep.RuntimePermission(
                permissions =
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                rationaleTitle = "Location Permission",
                rationaleMessage =
                    "BikeRide needs location access to detect when you're riding " +
                        "a bike and automatically enable ride mode.",
            )

        private fun addNotificationPermissionStepIfNeeded(steps: MutableList<PermissionStep>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                steps.add(
                    PermissionStep.RuntimePermission(
                        permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
                        rationaleTitle = "Notification Permission",
                        rationaleMessage =
                            "BikeRide needs notification permission to show you " +
                                "when ride mode is active and keep the service running.",
                    ),
                )
            }
        }

        private fun createSmsPermissionStep() =
            PermissionStep.RuntimePermission(
                permissions = listOf(Manifest.permission.SEND_SMS),
                rationaleTitle = "SMS Permission",
                rationaleMessage =
                    "BikeRide needs SMS permission to send automatic replies " +
                        "to callers when you're riding.",
            )

        private fun createPhoneContactsPermissionStep() =
            PermissionStep.RuntimePermission(
                permissions =
                    listOf(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                    ),
                rationaleTitle = "Phone & Contacts Permission",
                rationaleMessage =
                    "BikeRide needs access to phone state and contacts to " +
                        "identify incoming calls and manage call screening.",
            )

        private fun addCallScreeningRoleIfNeeded(steps: MutableList<PermissionStep>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                steps.add(
                    PermissionStep.RoleRequest(
                        role = RoleManager.ROLE_CALL_SCREENING,
                        rationaleTitle = "Call Screening Permission",
                        rationaleMessage =
                            "BikeRide needs the call screening role to automatically " +
                                "handle incoming calls while you're riding.",
                    ),
                )
            }
        }

        /**
         * Checks if a permission step is already granted.
         */
        fun isStepGranted(step: PermissionStep): Boolean =
            when (step) {
                is PermissionStep.RuntimePermission -> {
                    step.permissions.all { permission ->
                        ContextCompat.checkSelfPermission(
                            context,
                            permission,
                        ) == PackageManager.PERMISSION_GRANTED
                    }
                }
                is PermissionStep.RoleRequest -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val roleManager = context.getSystemService(RoleManager::class.java)
                        roleManager?.isRoleHeld(step.role) == true
                    } else {
                        true
                    }
                }
            }

        /**
         * Returns the next permission step that needs to be requested.
         */
        fun getNextPendingStep(): PermissionStep? = getPermissionSteps().firstOrNull { !isStepGranted(it) }

        /**
         * Checks if all permissions are granted.
         */
        fun areAllPermissionsGranted(): Boolean = getPermissionSteps().all { isStepGranted(it) }

        /**
         * Logs the current permission status for debugging.
         */
        fun logPermissionStatus() {
            getPermissionSteps().forEachIndexed { index, step ->
                val granted = isStepGranted(step)
                val name =
                    when (step) {
                        is PermissionStep.RuntimePermission -> step.permissions.joinToString()
                        is PermissionStep.RoleRequest -> step.role
                    }
                Timber.d("Permission step $index ($name): ${if (granted) "GRANTED" else "PENDING"}")
            }
        }
    }
