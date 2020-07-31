package com.leonpahole.workoutapplication.utils.exercises;

import android.media.TimedMetaData;

public class TimeDescriptor {
    Integer seconds, minutes, hours;

    public TimeDescriptor(Integer seconds, Integer minutes, Integer hours) {
        this.seconds = seconds == null ? 0 : seconds;
        this.minutes = minutes == null ? 0 : minutes;
        this.hours = hours == null ? 0 : hours;
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

    public static TimeDescriptor fromMiliseconds(long miliseconds) {
        int secondsTotal = (int) miliseconds / 1000;
        int hours = secondsTotal / (60 * 60);

        int remainderMandS = secondsTotal - hours * 60 * 60;

        int minutes = remainderMandS / 60;

        int seconds = remainderMandS - minutes * 60;

        return new TimeDescriptor(seconds, minutes, hours);
    }

    public static TimeDescriptor fromSeconds(int seconds) {
        return fromMiliseconds(seconds * 1000);
    }
}