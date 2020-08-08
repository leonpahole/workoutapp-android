package com.leonpahole.workoutapplication.utils.exercises;

import android.media.TimedMetaData;

import androidx.annotation.NonNull;

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

    public int toMS() {
        return toSeconds() * 1000;
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

    public void subtractSecond() {
        if (this.seconds == 0 && this.minutes == 0 && this.hours == 0) {
            return;
        }

        if (this.seconds == 0) {
            this.seconds = 59;

            if (this.minutes == 0) {
                this.minutes = 59;
                this.hours--;
            } else {
                this.minutes--;
            }
        } else {
            this.seconds--;
        }
    }

    public void addSecond() {
        if (this.seconds == 59) {
            this.seconds = 0;

            if (this.minutes == 59) {
                this.minutes = 0;
                this.hours++;
            } else {
                this.minutes++;
            }
        } else {
            this.seconds++;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%02d", this.getHours()) + ":" +
                String.format("%02d", this.getMinutes()) + ":" +
                String.format("%02d", this.getSeconds());
    }
}