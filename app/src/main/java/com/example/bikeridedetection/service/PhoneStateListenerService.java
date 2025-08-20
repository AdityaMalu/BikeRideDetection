package com.example.bikeridedetection.service;


import static android.content.Context.TELEPHONY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.Manifest;

public class PhoneStateListenerService extends Service{
    private static final String TAG = "BikePhoneListenerService";
    private TelephonyManager telephonyManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BikePhoneListenerService created");

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        BikeCallListener listener = new BikeCallListener();
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class BikeCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                Log.d(TAG, "PhoneStateListener: Incoming call from " + incomingNumber);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                boolean isSavedContact = false;
                Cursor cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null, null, null
                );

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        if (numberIndex == -1) continue; // skip if column not found

                        String contactNumber = cursor.getString(numberIndex);
                        if (contactNumber.replaceAll("\\s+", "").endsWith(incomingNumber.replaceAll("\\s+", ""))) {
                            isSavedContact = true;
                            break;
                        }
                    }
                    cursor.close();
                }


                if (isSavedContact) {
                    Log.d(TAG, "PhoneStateListener: Call from saved contact detected, sending SMS...");
                    // send SMS using SmsManager here
                }
            }
        }
    }
}
