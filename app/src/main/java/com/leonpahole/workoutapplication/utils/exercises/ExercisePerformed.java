package com.leonpahole.workoutapplication.utils.exercises;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExercisePerformed {
    Exercise exercise;
    List<SetPerformed> sets;

    public ExercisePerformed(Exercise exercise, List<SetPerformed> sets, boolean noSets) {
        this.exercise = exercise;
        this.sets = sets;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public List<SetPerformed> getSets() {
        return sets;
    }

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
}
