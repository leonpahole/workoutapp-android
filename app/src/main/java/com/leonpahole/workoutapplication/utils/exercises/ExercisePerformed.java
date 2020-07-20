package com.leonpahole.workoutapplication.utils.exercises;

import java.util.List;

public class ExercisePerformed {
    Exercise exercise;
    List<SetPerformed> sets;
    boolean noSets;

    public ExercisePerformed(Exercise exercise, List<SetPerformed> sets, boolean noSets) {
        this.exercise = exercise;
        this.sets = sets;
        this.noSets = noSets;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public List<SetPerformed> getSets() {
        return sets;
    }

    public boolean isNoSets() {
        return noSets;
    }
}
