package com.leonpahole.workoutapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.NumericHelper;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

public class SetLineCardio extends LinearLayout {
    DurationPicker setLineInputCardio_durationPicker;

    public SetLineCardio(Context context) {
        super(context);
        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.set_line_input_cardio, this);

        setLineInputCardio_durationPicker = findViewById(R.id.setLineInputCardio_durationPicker);
    }

    public int getHours() {
        return setLineInputCardio_durationPicker.getHours();
    }

    public int getMinutes() {
        return setLineInputCardio_durationPicker.getMinutes();
    }

    public int getSeconds() {
        return setLineInputCardio_durationPicker.getSeconds();
    }

    public void setHours(int hours) {
        setLineInputCardio_durationPicker.setHours(hours);
    }

    public void setMinutes(int minutes) {
        setLineInputCardio_durationPicker.setMinutes(minutes);
    }

    public void setSeconds(int seconds) {
        setLineInputCardio_durationPicker.setSeconds(seconds);
    }

    public TimeDescriptor getTime() {
        return new TimeDescriptor(getSeconds(), getMinutes(), getHours());
    }
}
