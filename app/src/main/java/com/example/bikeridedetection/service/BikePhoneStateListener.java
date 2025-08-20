package com.example.bikeridedetection.service;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;
import android.util.Log;

public class BikePhoneStateListener extends PhoneStateListener {

    private final Context context;
    private final TelephonyManager telephonyManager;

    private boolean isRinging = false;
    private String incomingNumber;

    public BikePhoneStateListener(Context context, TelephonyManager telephonyManager) {
        this.context = context;
        this.telephonyManager = telephonyManager;
    }

    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d("BikeApp", "Incoming call from: " + phoneNumber);
                isRinging = true;
                incomingNumber = phoneNumber;

                // Silence or reject logic you already implemented
                silenceOrRejectCall();

                // Send SMS reply
                sendAutoReply(getPhoneNumber(phoneNumber));


                break;

            case TelephonyManager.CALL_STATE_IDLE:
                isRinging = false;
                incomingNumber = null;
                break;
        }
    }

    private void silenceOrRejectCall() {
        try {
            // your existing rejection logic
            Log.d("BikeApp", "Call rejected/silenced");
        } catch (Exception e) {
            Log.e("BikeApp", "Error rejecting call", e);
        }
    }

    public static String getPhoneNumber(String input) {
        if (input == null || !input.startsWith("tel:")) {
            return null;
        }

        // Remove the "tel:" prefix
        String number = input.substring(4);

        // Decode "%2B" into "+"
        number = number.replace("%2B", "+");

        return number;
    }

    private void sendAutoReply(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    "I'm currently riding my bike and can't take calls.",
                    null,
                    null
            );
            Log.d("BikeApp", "Auto SMS sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e("BikeApp", "Failed to send SMS", e);
        }
    }
}
