package com.leonpahole.workoutapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {

    private static final String SHARED_PREFS = "workoutsy.storage";

    public static void saveJwt(Context context, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getJwt(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

    public static boolean isLoggedIn(Context context) {
        return getJwt(context) != null;
    }
}
