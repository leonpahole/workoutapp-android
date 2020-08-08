package com.leonpahole.workoutapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.NumericHelper;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

public class SetLineTimed extends LinearLayout {

    private RelativeLayout setLineTimed_headingWrapper;
    private TextView setLineTimed_txtSet;
    private ImageView setLineTimed_btnDelete;
    private LinearLayout setLineTimed_weightWrapper;

    private TextInputLayout setLineTimed_iptWeight, setLineTimed_iptHour, setLineTimed_iptMinutes,
            setLineTimed_iptSeconds;
    private TextView setLineTimed_txtWeightUnit;

    private TimeDescriptor time;

    SetLineOnClickListener deleteListener = null;
    private int setNumber;
    boolean isBodyweight = false;

    public SetLineTimed(Context context) {
        super(context);
        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.set_line_timed, this);

        setLineTimed_headingWrapper = findViewById(R.id.setLineTimed_headingWrapper);
        setLineTimed_txtSet = findViewById(R.id.setLineTimed_txtSet);
        setLineTimed_iptHour = findViewById(R.id.setLineTimed_iptHour);
        setLineTimed_iptMinutes = findViewById(R.id.setLineTimed_iptMinutes);
        setLineTimed_iptSeconds = findViewById(R.id.setLineTimed_iptSeconds);
        setLineTimed_btnDelete = findViewById(R.id.setLineTimed_btnDelete);
        setLineTimed_weightWrapper = findViewById(R.id.setLineTimed_weightWrapper);
        setLineTimed_iptWeight = findViewById(R.id.setLineTimed_iptWeight);
        setLineTimed_txtWeightUnit = findViewById(R.id.setLineTimed_txtWeightUnit);

        setLineTimed_btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener != null) {
                    deleteListener.onClick(v, setNumber);
                }
            }
        });
    }


    public void setShowHeader(boolean showHeader) {
        if (showHeader) {
            setLineTimed_headingWrapper.setVisibility(VISIBLE);
        } else {
            setLineTimed_headingWrapper.setVisibility(GONE);
        }

        invalidate();
        requestLayout();
    }

    public void setDeleteListener(SetLineOnClickListener listener) {
        deleteListener = listener;
    }

    public boolean validate() {
        String weightStr = getWeightStr();
        String hoursStr = getHoursStr();
        String minutesStr = getMinutesStr();
        String secondsStr = getSecondsStr();

        boolean isValid = true;

        if (weightStr.isEmpty() || !NumericHelper.isDouble(weightStr)) {
            setLineTimed_iptWeight.setError("Invalid value");
            isValid = false;
        } else {
            setLineTimed_iptWeight.setError(null);
            setLineTimed_iptWeight.setErrorEnabled(false);
        }

        if (hoursStr.isEmpty() || !NumericHelper.isInteger(hoursStr)) {
            setLineTimed_iptHour.setError("Invalid value");
            isValid = false;
        } else {
            setLineTimed_iptHour.setError(null);
            setLineTimed_iptHour.setErrorEnabled(false);
        }

        if (minutesStr.isEmpty() || !NumericHelper.isInteger(minutesStr)) {
            setLineTimed_iptMinutes.setError("Invalid value");
            isValid = false;
        } else {
            setLineTimed_iptMinutes.setError(null);
            setLineTimed_iptMinutes.setErrorEnabled(false);
        }

        if (secondsStr.isEmpty() || !NumericHelper.isInteger(secondsStr)) {
            setLineTimed_iptSeconds.setError("Invalid value");
            isValid = false;
        } else {
            setLineTimed_iptSeconds.setError(null);
            setLineTimed_iptSeconds.setErrorEnabled(false);
        }

        return isValid;
    }

    private String getWeightStr() {
        if (isBodyweight) {
            return "0";
        }

        return setLineTimed_iptWeight.getEditText().getText().toString();
    }

    private String getHoursStr() {
        return setLineTimed_iptHour.getEditText().getText().toString();
    }

    private String getMinutesStr() {
        return setLineTimed_iptMinutes.getEditText().getText().toString();
    }

    private String getSecondsStr() {
        return setLineTimed_iptSeconds.getEditText().getText().toString();
    }

    public double getWeight() {
        if (isBodyweight) {
            return 0;
        }

        return Double.parseDouble(getWeightStr());
    }

    public int getHours() {
        return Integer.parseInt(getHoursStr());
    }

    public int getMinutes() {
        return Integer.parseInt(getMinutesStr());
    }

    public int getSeconds() {
        return Integer.parseInt(getSecondsStr());
    }

    public TimeDescriptor getTime() {
        return new TimeDescriptor(getSeconds(), getMinutes(), getHours());
    }

    public void setWeight(Double weight) {
        if (isBodyweight || weight == null) {
            return;
        }

        setLineTimed_iptWeight.getEditText().setText(weight + "");

        invalidate();
        requestLayout();
    }

    public void setTime(TimeDescriptor time) {
        setLineTimed_iptHour.getEditText().setText(time.getHours() + "");
        setLineTimed_iptMinutes.getEditText().setText(time.getMinutes() + "");
        setLineTimed_iptSeconds.getEditText().setText(time.getSeconds() + "");

        this.time = time;

        invalidate();
        requestLayout();
    }

    public void setSetNumber(int number) {
        setLineTimed_txtSet.setText("Set " + number);
        setNumber = number;

        invalidate();
        requestLayout();
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setBodyweight(boolean bw) {
        isBodyweight = bw;

        if (isBodyweight) {
            setLineTimed_weightWrapper.setVisibility(GONE);
        } else {
            setLineTimed_weightWrapper.setVisibility(VISIBLE);
        }

        invalidate();
        requestLayout();
    }

    public boolean isBodyweight() {
        return isBodyweight;
    }

    public SetPerformed getSetPerformed() {
        if (isBodyweight) {
            return SetPerformed.timed(getTime());
        } else {
            return SetPerformed.weightedTimed(getTime(), getWeight());
        }
    }
}
