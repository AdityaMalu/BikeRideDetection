package com.example.bikeridedetection.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.bikeridedetection.domain.repository.BikeModeRepository;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;
import timber.log.Timber;

/**
 * Broadcast receiver for handling bike activity transitions.
 * Automatically enables/disables bike mode based on detected activity.
 */
@AndroidEntryPoint
public class BikeTransitionReceiver extends BroadcastReceiver {

    public static final String ACTION_BIKE_MODE_CHANGED = "com.example.bikeridedetection.BIKE_MODE_CHANGED";
    public static final String KEY_BIKE_MODE = "bike_mode";

    @Inject
    BikeModeRepository bikeModeRepository;

    private final Job supervisorJob = SupervisorKt.SupervisorJob(null);
    private final CoroutineScope receiverScope = CoroutineScopeHelper.createScope(supervisorJob, Dispatchers.getIO());

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (!ActivityTransitionResult.hasResult(intent)) {
            return;
        }

        ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
        if (result == null) {
            return;
        }

        PendingResult pendingResult = goAsync();
        List<ActivityTransitionEvent> events = result.getTransitionEvents();

        if (events.isEmpty()) {
            pendingResult.finish();
            return;
        }

        AtomicInteger pendingCount = new AtomicInteger(events.size());

        for (ActivityTransitionEvent event : events) {
            Timber.d("Detected transition: type=%d, transition=%d",
                    event.getActivityType(), event.getTransitionType());

            boolean bikeModeOn = event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER;

            CoroutineScopeHelper.handleTransitionAsync(
                    receiverScope,
                    bikeModeRepository,
                    bikeModeOn,
                    () -> {
                        Timber.d("Bike mode toggled: %s", bikeModeOn);

                        // Broadcast to other components
                        Intent broadcast = new Intent(ACTION_BIKE_MODE_CHANGED);
                        broadcast.putExtra(KEY_BIKE_MODE, bikeModeOn);
                        broadcast.setPackage(context.getPackageName());
                        context.sendBroadcast(broadcast);

                        // Finish when all events are processed
                        if (pendingCount.decrementAndGet() == 0) {
                            pendingResult.finish();
                        }
                    }
            );
        }
    }
}

