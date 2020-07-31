package com.leonpahole.workoutapplication.utils.exercises;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

public class SetPerformed {
    Integer repetitions;
    TimeDescriptor time;
    Double weight;
    WeightUnit weightUnit;

    // strength: 3 x 5 KG
    // bodyweight: 3 x 0 KG / LBS
    public SetPerformed(Integer repetitions, Double weight, WeightUnit weightUnit, TimeDescriptor time) {
        this.repetitions = repetitions;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.time = time;
    }

    public static SetPerformed bodyweight(Integer repetitions) {
        return new SetPerformed(repetitions, null, null, null);
    }

    public static SetPerformed strength(Integer repetitions, Double weight) {
        return new SetPerformed(repetitions, weight, WeightUnit.KILOGRAM, null);
    }

    public static SetPerformed stretching() {
        return new SetPerformed(null, null, null, null);
    }

    public static SetPerformed cardio(TimeDescriptor time) {
        return new SetPerformed(null, null, null, time);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (repetitions != null) {
            stringBuilder.append(repetitions + " x ");
        }

        if (time != null) {
            stringBuilder.append(time.getHours() + "h " + time.getMinutes() + "m " + time.getSeconds() + "s");
        } else if (weight != null && weight > 0) {
            stringBuilder.append(weight + weightUnit.getCode());
        } else {
            stringBuilder.append("BW");
        }

        return stringBuilder.toString();
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public TimeDescriptor getTime() {
        return time;
    }

    public Double getWeight() {
        return weight;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject set = new JSONObject();

        if (repetitions != null) {
            set.put("repetitions", repetitions);
        }

        if (weight != null) {
            set.put("weight", weight);
        }

        if (time != null) {
            set.put("time", time.toSeconds());
        }

        if (weightUnit != null) {
            set.put("weightUnit", weightUnit.getCode());
        }

        return set;
    }

    public static SetPerformed fromJson(JSONObject setJson) throws JSONException {
        try {
            Integer repetitions = null;
            TimeDescriptor time = null;
            Double weight = null;

            if (setJson.has("time") && !setJson.isNull("time")) {
                int timeSeconds = setJson.getInt("time");
                time = TimeDescriptor.fromSeconds(timeSeconds);
            }

            if (setJson.has("repetitions") && !setJson.isNull("repetitions")) {
                repetitions = setJson.getInt("repetitions");
            }

            if (setJson.has("weight") && !setJson.isNull("weight")) {
                weight = setJson.getDouble("weight");
            }

            return new SetPerformed(repetitions, weight, WeightUnit.KILOGRAM, time);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
