package com.example.bikeridedetection.util

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for managing runtime permissions.
 */
@Singleton
class PermissionHelper
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Requests SMS permission.
         *
         * @param activity The activity to request permission from
         */
        fun requestSmsPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SMS_PERMISSION,
            )
        }

        /**
         * Requests phone state and contacts permissions.
         *
         * @param activity The activity to request permission from
         */
        fun requestPhoneAndContactsPermissions(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                ),
                REQUEST_PHONE_CONTACTS_PERMISSION,
            )
        }

        /**
         * Requests the call screening role (Android 10+).
         *
         * @param activity The activity to request the role from
         */
        fun requestCallScreeningRole(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(RoleManager::class.java)
                if (roleManager?.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) == true) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    @Suppress("DEPRECATION")
                    activity.startActivityForResult(intent, REQUEST_CALL_ROLE)
                    Timber.d("Requested call screening role")
                }
            }
        }

        companion object {
            const val REQUEST_SMS_PERMISSION = 1001
            const val REQUEST_CALL_ROLE = 2001
            const val REQUEST_PHONE_CONTACTS_PERMISSION = 3001
            const val REQUEST_POST_NOTIFICATIONS = 4001
        }
    }
