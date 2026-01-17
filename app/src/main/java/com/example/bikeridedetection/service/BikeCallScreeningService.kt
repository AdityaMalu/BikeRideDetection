package com.example.bikeridedetection.service;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.Log;

import com.example.bikeridedetection.data.PreferencesRepository;

public class BikeCallScreeningService extends CallScreeningService {

    private static final String TAG = "BikeCallScreening";
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(TAG, "🚀 BikeCallScreeningService created");
    }

    @Override
    public void onScreenCall(Call.Details callDetails) {
        PreferencesRepository repo = new PreferencesRepository(context);
        boolean isBikeModeOn = repo.isBikeModeEnabled();

        String number = getPhoneNumber(String.valueOf(callDetails.getHandle()));

        Log.d(TAG, "Incoming call: " + number + " | BikeMode=" + isBikeModeOn);

        if (isBikeModeOn) {
            // Reject call
            CallResponse response = new CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipNotification(true)
                    .build();
            respondToCall(callDetails, response);
            Log.d(TAG, "❌ Call rejected");

            // Send SMS
            SmsService smsService = new SmsService(context);
            smsService.sendAutoReply(number);

        } else {
            Log.d(TAG, "✅ Call allowed");
        }
    }

    private static String getPhoneNumber(String input) {
        if (input == null || !input.startsWith("tel:")) {
            return null;
        }
        String number = input.substring(4);
        return number.replace("%2B", "+");
    }
}
