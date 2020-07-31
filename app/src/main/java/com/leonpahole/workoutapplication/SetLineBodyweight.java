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

public class SetLineBodyweight extends LinearLayout {
    RelativeLayout setLineInputBodyweight_headingWrapper;
    TextView setLineInputBodyweight_txtSet;
    ImageView setLineInputBodyweight_btnDelete;

    TextInputLayout setLineInputBodyweight_iptReps;

    SetLineOnClickListener deleteListener = null;

    private int setNumber;

    public SetLineBodyweight(Context context) {
        super(context);
        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.set_line_input_bodyweight, this);

        setLineInputBodyweight_headingWrapper = findViewById(R.id.setLineInputBodyweight_headingWrapper);
        setLineInputBodyweight_txtSet = findViewById(R.id.setLineInputBodyweight_txtSet);
        setLineInputBodyweight_btnDelete = findViewById(R.id.setLineInputBodyweight_btnDelete);

        setLineInputBodyweight_iptReps = findViewById(R.id.setLineInputBodyweight_iptReps);

        setLineInputBodyweight_btnDelete.setOnClickListener(new View.OnClickListener() {
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
            setLineInputBodyweight_headingWrapper.setVisibility(VISIBLE);
        } else {
            setLineInputBodyweight_headingWrapper.setVisibility(GONE);
        }

        invalidate();
        requestLayout();
    }

    public void setDeleteListener(SetLineOnClickListener listener) {
        deleteListener = listener;
    }

    public boolean validate() {
        String repsStr = getRepsStr();

        boolean isValid = true;

        if (repsStr.isEmpty() || !NumericHelper.isInteger(repsStr)) {
            setLineInputBodyweight_iptReps.setError("Invalid value");
            isValid = false;
        } else {
            setLineInputBodyweight_iptReps.setError(null);
            setLineInputBodyweight_iptReps.setErrorEnabled(false);
        }

        return isValid;
    }

    private String getRepsStr() {
        return setLineInputBodyweight_iptReps.getEditText().getText().toString();
    }

    public int getReps() {
        return Integer.parseInt(getRepsStr());
    }

    public void setSetNumber(int number) {
        setLineInputBodyweight_txtSet.setText("Set " + number);
        setNumber = number;
    }

    public int getSetNumber() {
        return setNumber;
    }
}
