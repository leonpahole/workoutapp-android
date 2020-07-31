package com.leonpahole.workoutapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class DurationPicker extends LinearLayout {

    private NumberPicker durationPicker_hour, durationPicker_minute, durationPicker_second;

    boolean mShowHours;

    public DurationPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        initControl(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DurationPicker,
                0, 0);

        try {
            mShowHours = a.getBoolean(R.styleable.DurationPicker_showHours, true);
        } finally {
            a.recycle();
        }
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.duration_picker, this);

        durationPicker_hour = findViewById(R.id.durationPicker_hour);
        durationPicker_minute = findViewById(R.id.durationPicker_minute);
        durationPicker_second = findViewById(R.id.durationPicker_second);

        durationPicker_hour.setMinValue(0);
        durationPicker_hour.setMaxValue(23);

        durationPicker_minute.setMinValue(0);
        durationPicker_minute.setMaxValue(59);

        durationPicker_second.setMinValue(0);
        durationPicker_second.setMaxValue(59);
    }

    public boolean isShowHours() {
        return mShowHours;
    }

    public void setShowHours(boolean showHours) {
        mShowHours = showHours;
        invalidate();
        requestLayout();
    }

    public int getHours() {
        return durationPicker_hour.getValue();
    }

    public int getMinutes() {
        return durationPicker_minute.getValue();
    }

    public int getSeconds() {
        return durationPicker_second.getValue();
    }

    public void setHours(int hours) {
        durationPicker_hour.setValue(hours);
    }

    public void setMinutes(int minutes) {
        durationPicker_minute.setValue(minutes);
    }

    public void setSeconds(int seconds) {
        durationPicker_second.setValue(seconds);
    }
}
