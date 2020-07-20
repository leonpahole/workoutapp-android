package com.leonpahole.workoutapplication.utils.exercises;


import androidx.annotation.NonNull;

public class SetPerformed {
    Integer repetitions, time;
    Double weight;
    WeightUnit weightUnit;

    // strength: 3 x 5 KG
    // bodyweight: 3 x 0 KG / LBS
    public SetPerformed(Integer repetitions, Double weight, WeightUnit weightUnit, Integer time) {
        this.repetitions = repetitions;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.time = time;
    }

    public static SetPerformed bodyweight(Integer repetitions) {
        return new SetPerformed(repetitions, null, null, null);
    }

    public static SetPerformed strength(Integer repetitions, Double weight, WeightUnit weightUnit) {
        return new SetPerformed(repetitions, weight, weightUnit, null);
    }

    public static SetPerformed stretching() {
        return new SetPerformed(null, null, null, null);
    }

    public static SetPerformed cardio(Integer time) {
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
            stringBuilder.append(time + "s x ");
        }

        if (weight != null && weight > 0) {
            stringBuilder.append(weight + weightUnit.getCode());
        } else {
            stringBuilder.append("BW");
        }

        return stringBuilder.toString();
    }
}
