package com.example.fmdriver.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUtils implements AppConstants {

    //ADROID ID
    // -------------------------------------------------
    public static void updateAndroidId(Context context, String androidId) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("androidId", androidId);
        editor.commit();
    }

    public static String getAndroidId(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getString("androidId", "");
    }
    // -------------------------------------------------

    //DEVICE ID
    // -------------------------------------------------
    public static void updateDeviceId(Context context, String androidId) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("deviceId", androidId);
        editor.commit();
    }
}
