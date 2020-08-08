package com.leonpahole.workoutapplication.utils.exercises;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    long id;
    String name;
    ExerciseCategory category;
    Integer authorId;

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("category", category.getCode())
                .put("authorId", authorId);
    }

    public static JSONArray toJsonArray(List<Exercise> exercises) {
        JSONArray arr = new JSONArray();
        for (Exercise exercise : exercises) {
            try {
                arr.put(exercise.toJson());
            } catch (JSONException e) {

            } catch (NullPointerException e) {
                System.out.println(exercise.toString());
            }
        }

        return arr;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
