package com.example.bikeridedetection.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bikeridedetection.utils.Constants;

public class PreferencesRepository {
    private final SharedPreferences prefs;

    public PreferencesRepository(Context appContext) {
        this.prefs = appContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isBikeModeEnabled() {
        return prefs.getBoolean(Constants.KEY_BIKE_MODE_ENABLED, false);
    }

    public void setBikeModeEnabled(boolean enabled) {
        prefs.edit().putBoolean(Constants.KEY_BIKE_MODE_ENABLED, enabled).apply();
    }
}
