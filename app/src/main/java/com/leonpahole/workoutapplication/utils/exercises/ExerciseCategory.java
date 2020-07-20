package com.leonpahole.workoutapplication.utils.exercises;

public enum ExerciseCategory {
    STRENGTH("STR"), BODYWEIGHT("BW"), STRETCHING("ST"), CARDIO("CA");

    private String code;

    ExerciseCategory(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ExerciseCategory of(String code) {
        for (int i = 0; i < ExerciseCategory.values().length; i++) {
            if (code.equals(ExerciseCategory.values()[i].getCode())) {
                return ExerciseCategory.values()[i];
            }
        }

        return null;
    }
}
