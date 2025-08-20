package com.example.bikeridedetection.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikeridedetection.R;
import com.example.bikeridedetection.data.PreferencesRepository;
import com.example.bikeridedetection.service.BikePhoneStateListener;
import com.example.bikeridedetection.manager.PermissionManager;
import com.example.bikeridedetection.viewmodel.MainViewModel;
import com.example.bikeridedetection.viewmodel.MainViewModelFactory;

public class MainActivity extends ComponentActivity {

    private MainViewModel viewModel;
    private SwitchCompat switchBikeMode;
    private TextView statusText;
    private boolean updatingFromViewModel = false;
    private BikePhoneStateListener bikeListener;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("BikeApp", "MainActivity created");

        // Repo + VM
        PreferencesRepository repo = new PreferencesRepository(getApplicationContext());
        MainViewModelFactory factory = new MainViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        // UI
        switchBikeMode = findViewById(R.id.switchBikeMode);
        statusText = findViewById(R.id.statusText);

        // Setup telephony
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        bikeListener = new BikePhoneStateListener(this,telephonyManager);

        PermissionManager.requestNotificationPermission(this);

        // Observe state → update UI + start/stop listener
        viewModel.getBikeModeEnabled().observe(this, enabled -> {
            updatingFromViewModel = true;
            boolean isOn = Boolean.TRUE.equals(enabled);
            if (switchBikeMode.isChecked() != isOn) {
                switchBikeMode.setChecked(isOn);
            }
            statusText.setText("Status: " + (isOn ? "ON" : "OFF"));
            updatingFromViewModel = false;

            if (isOn) {
                startPhoneListener();
                Intent svc = new Intent(getApplicationContext(), com.example.bikeridedetection.service.NotificationService.class);
                Log.d("MainActivity", "Starting BikeModeService foreground");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(svc);
                } else {
                    startService(svc);
                }
            } else {
                stopPhoneListener();
                Intent svc = new Intent(getApplicationContext(), com.example.bikeridedetection.service.NotificationService.class);
                Log.d("MainActivity", "Stopping BikeModeService");
                stopService(svc);
            }
        });

        // User toggles switch → update VM
        switchBikeMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!updatingFromViewModel) {
                viewModel.setBikeModeEnabled(isChecked);
            }
        });

        // Request permissions
        PermissionManager.requestSmsPermission(this);
        PermissionManager.requestPhoneAndContactsPermissions(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            PermissionManager.requestCallScreeningRole(this);
        }

        handleIntentAction(getIntent());

    }

    private void startPhoneListener() {
        Log.d("BikeApp", "Phone listener started");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            telephonyManager.listen(bikeListener, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            PermissionManager.requestPhoneAndContactsPermissions(this);
        }
    }

    private void stopPhoneListener() {
        Log.d("BikeApp", "Phone listener stopped");
        telephonyManager.listen(bikeListener, PhoneStateListener.LISTEN_NONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPhoneListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionManager.REQUEST_CALL_ROLE) {
            Log.d("BikeApp", "Call screening role request finished");
        }
    }

    private void handleIntentAction(Intent intent) {
        if (intent != null && "com.example.bikeridedetection.ACTION_BIKE_MODE_OFF".equals(intent.getAction())) {
            Log.d("MainActivity", "🚴 Notification clicked → Turning OFF Bike Mode");
            viewModel.setBikeModeEnabled(false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("MainActivity", "onNewIntent() called with action=" + intent.getAction());

        if ("com.example.bikeridedetection.ACTION_BIKE_MODE_OFF".equals(intent.getAction())) {
            Log.d("MainActivity", "🚴 Notification clicked → Turning OFF Bike Mode");
            viewModel.setBikeModeEnabled(false);
        }
    }




}
