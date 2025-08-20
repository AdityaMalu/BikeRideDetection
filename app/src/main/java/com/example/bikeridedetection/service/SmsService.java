package com.example.bikeridedetection.service;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsService {
    private final Context context;

    public SmsService(Context context) {
        this.context = context;
    }

    public void sendAutoReply(String phoneNumber) {
        if (phoneNumber == null) return;

        String message = "I'm currently riding my bike 🚴, will call you back later.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SmsService", "✅ SMS sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e("SmsService", "❌ Failed to send SMS: " + e.getMessage(), e);
        }
    }
}
