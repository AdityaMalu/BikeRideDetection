package com.example.bikeridedetection.service;

import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bikeridedetection.domain.usecase.GetBikeModeUseCase;
import com.example.bikeridedetection.domain.usecase.SendAutoReplyUseCase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;
import timber.log.Timber;

/**
 * Call screening service that rejects calls when bike mode is enabled.
 * Sends an auto-reply SMS to the caller.
 */
@AndroidEntryPoint
public class BikeCallScreeningService extends CallScreeningService {

    private static final String TEL_URI_PREFIX = "tel:";

    @Inject
    GetBikeModeUseCase getBikeModeUseCase;

    @Inject
    SendAutoReplyUseCase sendAutoReplyUseCase;

    private Job supervisorJob;
    private CoroutineScope serviceScope;

    @Override
    public void onCreate() {
        super.onCreate();
        supervisorJob = SupervisorKt.SupervisorJob(null);
        serviceScope = CoroutineScopeHelper.createScope(supervisorJob, Dispatchers.getMain());
        Timber.d("BikeCallScreeningService created");
    }

    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {
        String handle = callDetails.getHandle() != null ? callDetails.getHandle().toString() : null;
        String phoneNumber = extractPhoneNumber(handle);

        CoroutineScopeHelper.screenCallAsync(
                serviceScope,
                getBikeModeUseCase,
                sendAutoReplyUseCase,
                phoneNumber,
                (bikeMode, smsResult) -> {
                    Timber.d("Incoming call: %s | BikeMode=%s", phoneNumber, bikeMode.isEnabled());

                    if (bikeMode.isEnabled()) {
                        rejectCall(callDetails);
                        if (smsResult != null) {
                            Timber.d("Auto-reply result: %s", smsResult);
                        }
                    } else {
                        allowCall(callDetails);
                    }
                }
        );
    }

    private void rejectCall(@NonNull Call.Details callDetails) {
        CallResponse response = new CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
                .build();
        respondToCall(callDetails, response);
        Timber.d("Call rejected");
    }

    private void allowCall(@NonNull Call.Details callDetails) {
        CallResponse response = new CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .build();
        respondToCall(callDetails, response);
        Timber.d("Call allowed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (supervisorJob != null) {
            supervisorJob.cancel(null);
        }
        Timber.d("BikeCallScreeningService destroyed");
    }

    /**
     * Extracts the phone number from a tel: URI.
     *
     * @param input The tel: URI string
     * @return The extracted phone number, or null if invalid
     */
    @Nullable
    public static String extractPhoneNumber(@Nullable String input) {
        if (input == null || !input.startsWith(TEL_URI_PREFIX)) {
            return null;
        }
        return input.substring(TEL_URI_PREFIX.length()).replace("%2B", "+");
    }
}

