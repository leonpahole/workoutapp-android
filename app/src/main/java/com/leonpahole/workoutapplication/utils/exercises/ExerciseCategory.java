package com.leonpahole.workoutapplication.utils.exercises;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public enum ExerciseCategory {
    @SerializedName("STR")
    STRENGTH("STR", "STRENGTH"),

    @SerializedName("BW")
    BODYWEIGHT("BW", "BODYWEIGHT"),

    @SerializedName("ST")
    STRETCHING("ST", "STRETCHING"),

    @SerializedName("CA")
    CARDIO("CA", "CARDIO"),

    @SerializedName("TIM")
    TIMED("TIM", "TIMED"),

    @SerializedName("WTIM")
    WEIGHTED_TIMED("WTIM", "WEIGHTED TIMED");

    private String code;
    private String name;

    ExerciseCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ExerciseCategory of(String code) {
        for (int i = 0; i < ExerciseCategory.values().length; i++) {
            if (code.equals(ExerciseCategory.values()[i].getCode())) {
                return ExerciseCategory.values()[i];
            }
        }

        return null;
    }


    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
