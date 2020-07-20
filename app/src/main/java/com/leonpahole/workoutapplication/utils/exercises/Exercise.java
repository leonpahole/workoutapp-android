package com.leonpahole.workoutapplication.utils.exercises;

public class Exercise {
    long id;
    String name;
    ExerciseCategory category;

    public Exercise(long id, String name, ExerciseCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ExerciseCategory getCategory() {
        return category;
    }
}
