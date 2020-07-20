package com.leonpahole.workoutapplication.utils.exercises;

public enum WeightUnit {
    KILOGRAM("KG"), POUND("LB");

    private String code;

    WeightUnit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static WeightUnit of(String code) {
        for (int i = 0; i < WeightUnit.values().length; i++) {
            if (code.equals(WeightUnit.values()[i].getCode())) {
                return WeightUnit.values()[i];
            }
        }

        return null;
    }
}
