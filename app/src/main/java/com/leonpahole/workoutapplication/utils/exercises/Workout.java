package com.leonpahole.workoutapplication.utils.exercises;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Workout {
    long id;
    String name, comment;
    String createdAt, startDate, startTime, endTime;
    List<ExercisePerformed> exercisesPerformed;

    public static Workout fromJson(Context context, JSONObject workoutJson) {

        try {

            List<Exercise> exercises = LocalStorage.getExercises(context);

            DateFormat createdAtFormatUTC = new SimpleDateFormat("dd. MM. yyyy HH:mm");
            createdAtFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date createdAtUTC = createdAtFormatUTC.parse(workoutJson.getString("createdAt"));

            DateFormat createdAtFormatLocal = new SimpleDateFormat("dd. MM. yyyy HH:mm");
            createdAtFormatLocal.setTimeZone(TimeZone.getDefault());

            String createdAt = createdAtFormatLocal.format(createdAtUTC);

            List<ExercisePerformed> exercisesPerformed = new ArrayList<>();
            JSONArray exercisesPerformedJson = workoutJson.getJSONArray("exercisesPerformed");
            for (int i = 0; i < exercisesPerformedJson.length(); i++) {
                ExercisePerformed exercisePerformed = ExercisePerformed.fromJson(exercisesPerformedJson.getJSONObject(i), exercises);

                if (exercisePerformed != null) {
                    exercisesPerformed.add(exercisePerformed);
                }
            }

            return Workout.builder()
                    .id(workoutJson.getLong("id"))
                    .name(workoutJson.getString("name"))
                    .comment(workoutJson.getString("comment"))
                    .createdAt(createdAt)
                    .startDate(workoutJson.getString("startDate"))
                    .startTime(workoutJson.has("startTime") && !workoutJson.isNull("startTime") ?
                            workoutJson.getString("startTime") : null)
                    .endTime(workoutJson.has("endTime") && !workoutJson.isNull("endTime") ?
                            workoutJson.getString("endTime") : null)
                    .exercisesPerformed(exercisesPerformed)
                    .build();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Workout> fromJson(Context context, JSONArray workoutsJson) {
        List<Workout> workouts = new ArrayList<>();

        for (int i = 0; i < workoutsJson.length(); i++) {
            try {
                Workout workout = Workout.fromJson(context, workoutsJson.getJSONObject(i));
                if (workout != null) {
                    workouts.add(workout);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return workouts;
    }
}
