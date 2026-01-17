package com.example.bikeridedetection.ui.activity

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bikeridedetection.R
import com.example.bikeridedetection.databinding.ActivityPermissionFlowBinding
import com.example.bikeridedetection.util.PermissionManager
import com.example.bikeridedetection.util.PermissionStep
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Activity that handles sequential permission requests during first launch.
 * Shows rationale dialogs before each permission request.
 */
@AndroidEntryPoint
class PermissionFlowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionFlowBinding

    @Inject
    lateinit var permissionManager: PermissionManager

    private var currentStepIndex = 0
    private lateinit var permissionSteps: List<PermissionStep>

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = results.all { it.value }
            Timber.d("Permission result: allGranted=$allGranted, results=$results")
            onPermissionResult(allGranted)
        }

    private val roleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val granted = result.resultCode == RESULT_OK
            Timber.d("Role result: granted=$granted")
            onPermissionResult(granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionSteps = permissionManager.getPermissionSteps()
        permissionManager.logPermissionStatus()

        // Find the first pending permission
        currentStepIndex = permissionSteps.indexOfFirst { !permissionManager.isStepGranted(it) }

        if (currentStepIndex == -1) {
            // All permissions already granted
            Timber.d("All permissions already granted")
            finishWithSuccess()
            return
        }

        updateProgress()
        requestCurrentPermission()
    }

    private fun updateProgress() {
        val totalSteps = permissionSteps.size
        val completedSteps = permissionSteps.count { permissionManager.isStepGranted(it) }
        binding.progressBar.max = totalSteps
        binding.progressBar.progress = completedSteps
        binding.progressText.text =
            getString(
                R.string.permission_progress,
                completedSteps + 1,
                totalSteps,
            )
    }

    private fun requestCurrentPermission() {
        if (currentStepIndex >= permissionSteps.size) {
            finishWithSuccess()
            return
        }

        val step = permissionSteps[currentStepIndex]
        showRationaleDialog(step)
    }

    private fun showRationaleDialog(step: PermissionStep) {
        val (title, message) =
            when (step) {
                is PermissionStep.RuntimePermission -> step.rationaleTitle to step.rationaleMessage
                is PermissionStep.RoleRequest -> step.rationaleTitle to step.rationaleMessage
            }

        binding.permissionTitle.text = title
        binding.permissionDescription.text = message

        binding.grantButton.setOnClickListener {
            launchPermissionRequest(step)
        }

        binding.skipButton.setOnClickListener {
            onPermissionResult(false)
        }
    }

    private fun launchPermissionRequest(step: PermissionStep) {
        when (step) {
            is PermissionStep.RuntimePermission -> {
                permissionLauncher.launch(step.permissions.toTypedArray())
            }
            is PermissionStep.RoleRequest -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val roleManager = getSystemService(RoleManager::class.java)
                    if (roleManager?.isRoleAvailable(step.role) == true) {
                        val intent = roleManager.createRequestRoleIntent(step.role)
                        roleLauncher.launch(intent)
                    } else {
                        onPermissionResult(false)
                    }
                } else {
                    onPermissionResult(true)
                }
            }
        }
    }

    private fun onPermissionResult(granted: Boolean) {
        if (granted) {
            Timber.d("Permission step $currentStepIndex granted")
        } else {
            Timber.w("Permission step $currentStepIndex denied or skipped")
        }

        // Move to next step
        currentStepIndex++
        updateProgress()

        // Find next pending permission
        while (currentStepIndex < permissionSteps.size &&
            permissionManager.isStepGranted(permissionSteps[currentStepIndex])
        ) {
            currentStepIndex++
        }

        if (currentStepIndex >= permissionSteps.size) {
            finishWithSuccess()
        } else {
            requestCurrentPermission()
        }
    }

    private fun finishWithSuccess() {
        Timber.d("Permission flow completed")
        setResult(RESULT_OK)
        finish()
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, PermissionFlowActivity::class.java)
    }
}
