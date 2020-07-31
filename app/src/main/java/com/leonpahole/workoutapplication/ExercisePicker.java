package com.leonpahole.workoutapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;

import java.util.ArrayList;

public class ExercisePicker extends LinearLayout {

    private TextInputLayout exercisePicker_iptExerciseLayout;
    private AutoCompleteTextView exercisePicker_iptExercise;

    private ArrayList<Exercise> exercises;

    public ExercisePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.duration_picker, this);

        exercisePicker_iptExerciseLayout = findViewById(R.id.exercisePicker_iptExerciseLayout);
        exercisePicker_iptExercise = findViewById(R.id.exercisePicker_iptExercise);

        exercises = LocalStorage.getExercises(context);

        ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(getContext(), R.layout.exercise_dropdown_item, exercises);
        exercisePicker_iptExercise.setAdapter(adapter);

        exercisePicker_iptExercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {

                Exercise customObject = (Exercise) parent.getItemAtPosition(pos);
                int index = -1;
                for (int i = 0; i < exercises.size(); i++) {
                    if (exercises.get(i).getId() == customObject.getId()) {
                        index = i;
                        break;
                    }
                }

                if (index < 0) {
                    return;
                }
            }
        });
    }
}
