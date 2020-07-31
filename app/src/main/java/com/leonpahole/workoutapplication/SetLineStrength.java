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
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;

public class SetLineStrength extends LinearLayout {

    private RelativeLayout setLineInputStrength_headingWrapper;
    private TextView setLineInputStrength_txtSet;
    private ImageView setLineInputStrength_btnDelete;

    private TextInputLayout setLineInputStrength_iptReps, setLineInputStrength_iptWeight;
    private TextView setLineInputStrength_txtWeightUnit, setLineInputStrength_txtCross;

    SetLineOnClickListener deleteListener = null;
    private int setNumber;
    boolean isBodyweight = false;

    public SetLineStrength(Context context) {
        super(context);
        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.set_line_input_strength, this);

        setLineInputStrength_headingWrapper = findViewById(R.id.setLineInputStrength_headingWrapper);
        setLineInputStrength_txtSet = findViewById(R.id.setLineInputStrength_txtSet);
        setLineInputStrength_btnDelete = findViewById(R.id.setLineInputStrength_btnDelete);

        setLineInputStrength_iptReps = findViewById(R.id.setLineInputStrength_iptReps);
        setLineInputStrength_iptWeight = findViewById(R.id.setLineInputStrength_iptWeight);

        setLineInputStrength_txtWeightUnit = findViewById(R.id.setLineInputStrength_txtWeightUnit);
        setLineInputStrength_txtCross = findViewById(R.id.setLineInputStrength_txtCross);

        setLineInputStrength_btnDelete.setOnClickListener(new View.OnClickListener() {
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
            setLineInputStrength_headingWrapper.setVisibility(VISIBLE);
        } else {
            setLineInputStrength_headingWrapper.setVisibility(GONE);
        }

        invalidate();
        requestLayout();
    }

    public void setDeleteListener(SetLineOnClickListener listener) {
        deleteListener = listener;
    }

    public boolean validate() {
        String weightStr = getWeightStr();
        String repsStr = getRepsStr();

        boolean isValid = true;

        if (repsStr.isEmpty() || !NumericHelper.isInteger(repsStr)) {
            setLineInputStrength_iptReps.setError("Invalid value");
            isValid = false;
        } else {
            setLineInputStrength_iptReps.setError(null);
            setLineInputStrength_iptReps.setErrorEnabled(false);
        }

        if (weightStr.isEmpty() || !NumericHelper.isDouble(weightStr)) {
            setLineInputStrength_iptWeight.setError("Invalid value");
            isValid = false;
        } else {
            setLineInputStrength_iptWeight.setError(null);
            setLineInputStrength_iptWeight.setErrorEnabled(false);
        }

        return isValid;
    }

    private String getWeightStr() {
        if (isBodyweight) {
            return "0";
        }

        return setLineInputStrength_iptWeight.getEditText().getText().toString();
    }

    private String getRepsStr() {
        return setLineInputStrength_iptReps.getEditText().getText().toString();
    }

    public double getWeight() {
        if (isBodyweight) {
            return 0;
        }

        return Double.parseDouble(getWeightStr());
    }

    public int getReps() {
        return Integer.parseInt(getRepsStr());
    }

    public void setWeight(double weight) {
        if (isBodyweight) {
            return;
        }

        setLineInputStrength_iptWeight.getEditText().setText(weight + "");

        invalidate();
        requestLayout();
    }

    public void setReps(int reps) {
        setLineInputStrength_iptReps.getEditText().setText(reps + "");

        invalidate();
        requestLayout();
    }

    public void setSetNumber(int number) {
        setLineInputStrength_txtSet.setText("Set " + number);
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
            setLineInputStrength_txtWeightUnit.setText("x BW");
            setLineInputStrength_txtCross.setVisibility(GONE);
            setLineInputStrength_iptWeight.setVisibility(GONE);
        } else {
            setLineInputStrength_txtWeightUnit.setText("KG");
            setLineInputStrength_txtCross.setVisibility(VISIBLE);
            setLineInputStrength_iptWeight.setVisibility(VISIBLE);
        }

        invalidate();
        requestLayout();
    }

    public boolean isBodyweight() {
        return isBodyweight;
    }

    public SetPerformed getSetPerformed() {
        if (isBodyweight) {
            return SetPerformed.bodyweight(getReps());
        } else {
            return SetPerformed.strength(getReps(), getWeight());
        }
    }
}
