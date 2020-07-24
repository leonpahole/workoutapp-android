package com.leonpahole.workoutapplication.utils.exercises;

public class TimeDescriptor {
    int seconds, minutes, hours;

    public TimeDescriptor(int seconds, int minutes, int hours) {
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    public int toSeconds() {
        return 60 * 60 * hours + 60 * minutes + seconds;
    }
}