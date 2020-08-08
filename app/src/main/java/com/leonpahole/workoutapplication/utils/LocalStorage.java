package com.leonpahole.workoutapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalStorage {

    private static final String SHARED_PREFS = "workoutsy.storage";

    public static void saveJwt(Context context, @Nullable String token, @Nullable JSONObject user) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.putString("user", user == null ? null : user.toString());
        editor.apply();
    }

    public static String getJwt(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

    public static boolean isLoggedIn(Context context) {
        return getJwt(context) != null;
    }

    public static void saveExercises(Context context, List<Exercise> exercises) {
        saveExercises(context, Exercise.toJsonArray(exercises));
    }

    public static void saveExercises(Context context, JSONArray exercises) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("exercises", exercises.toString());
        editor.apply();
    }

    public static boolean hasExercises(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String exercisesJson = sharedPref.getString("exercises", null);
        return exercisesJson != null;
    }

    public static ArrayList<Exercise> getExercises(Context context) {
        if (!hasExercises(context)) {
            return new ArrayList<>();
        }

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String exercisesJson = sharedPref.getString("exercises", null);

        return GsonUtil.getGsonParser().fromJson(exercisesJson, new TypeToken<ArrayList<Exercise>>() {
        }.getType());
    }

    public static User getUser(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String userStr = sharedPref.getString("user", null);
        return GsonUtil.getGsonParser().fromJson(userStr, new TypeToken<User>() {
        }.getType());
    }

}
