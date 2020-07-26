package com.leonpahole.workoutapplication.utils.exercises;

import android.content.Context;

import com.leonpahole.workoutapplication.utils.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExercisePerformed {
    Exercise exercise;
    List<SetPerformed> sets;

    public boolean isNoSets() {
        return exercise.getCategory() == ExerciseCategory.CARDIO ||
                exercise.getCategory() == ExerciseCategory.STRETCHING;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject exercisePerformed = new JSONObject();

        exercisePerformed.put("exerciseId", exercise.getId());
        exercisePerformed.put("noSets", isNoSets());

        JSONArray setsJson = new JSONArray();

        for (SetPerformed set : sets) {
            setsJson.put(set.toJson());
        }

        exercisePerformed.put("sets", setsJson);

        return exercisePerformed;
    }

    public static JSONArray toJson(List<ExercisePerformed> exercisesPerformed) throws JSONException {
        JSONArray exercisesPerformedJson = new JSONArray();

        for (ExercisePerformed exercisePerformed : exercisesPerformed) {
            exercisesPerformedJson.put(exercisePerformed.toJson());
        }

        return exercisesPerformedJson;
    }

    public static ExercisePerformed fromJson(JSONObject exercisePerformedJson, List<Exercise> exercises) throws JSONException {

        Exercise exercise = null;

        int exerciseId = exercisePerformedJson.getInt("exerciseId");

        for (Exercise e : exercises) {
            if (e.getId() == exerciseId) {
                exercise = e;
                break;
            }
        }

        if (exercise == null) {
            return null;
        }

        List<SetPerformed> sets = new ArrayList<>();
        JSONArray setsJson = exercisePerformedJson.getJSONArray("sets");

        for (int i = 0; i < setsJson.length(); i++) {
            SetPerformed set = SetPerformed.fromJson(setsJson.getJSONObject(i));
            if (set != null) {
                sets.add(set);
            }
        }

        return new ExercisePerformed(exercise, sets);
    }
}
