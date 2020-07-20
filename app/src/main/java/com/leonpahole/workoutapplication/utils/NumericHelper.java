package com.leonpahole.workoutapplication.utils;

public class NumericHelper {

    public static boolean isInteger(String val) {
        try {
            Integer.parseInt(val);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static boolean isDouble(String val) {
        try {
            Double.parseDouble(val);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }
}
