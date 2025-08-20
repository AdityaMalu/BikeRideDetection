package com.example.bikeridedetection.manager;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class PermissionManager {

    public static final int REQUEST_SMS_PERMISSION = 1001;
    public static final int REQUEST_CALL_ROLE = 2001;
    public static final int REQUEST_PHONE_CONTACTS_PERMISSION = 3001;
    public static final int REQUEST_POST_NOTIFICATIONS = 4001;


    // Request SEND_SMS permission
    public static void requestSmsPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.SEND_SMS},
                REQUEST_SMS_PERMISSION
        );
    }

    // Request READ_PHONE_STATE and READ_CONTACTS for detecting all calls
    public static void requestPhoneAndContactsPermissions(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS
                },
                REQUEST_PHONE_CONTACTS_PERMISSION
        );
    }

    // Request Call Screening Role (Android 10+)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void requestCallScreeningRole(Activity activity) {
        RoleManager roleManager = (RoleManager) activity.getSystemService(Context.ROLE_SERVICE);
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
            activity.startActivityForResult(intent, REQUEST_CALL_ROLE);
        }
    }

    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) { // Android 13+
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_POST_NOTIFICATIONS
            );
        }
    }
}
