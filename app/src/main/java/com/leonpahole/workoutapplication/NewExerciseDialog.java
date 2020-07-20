package com.leonpahole.workoutapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.NumericHelper;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.WeightUnit;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NewExerciseDialog extends DialogFragment {

    private TextInputLayout exerciseDialog_iptExerciseLayout;
    private AutoCompleteTextView exerciseDialog_iptExercise;
    private List<Exercise> exercises;
    private Exercise exerciseSelected;

    private NewExerciseDialogListener listener;

    Integer setLineInputViewId = null;

    LinearLayout exerciseDialog_setsLayout;
    Button exerciseDialog_btnAddSet;
    ScrollView exerciseDialog_setsLayoutScroll;

    int setCount = 0;

    public void setListener(NewExerciseDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_exercise_dialog, null);

        exercises = new ArrayList<>();
        exercises.add(new Exercise(1, "Pull ups", ExerciseCategory.BODYWEIGHT));
        exercises.add(new Exercise(2, "Bench press", ExerciseCategory.STRENGTH));
        exercises.add(new Exercise(3, "Hamstrings", ExerciseCategory.STRETCHING));
        exercises.add(new Exercise(4, "Stationary bike", ExerciseCategory.CARDIO));
        exercises.add(new Exercise(5, "Push ups", ExerciseCategory.BODYWEIGHT));
        exercises.add(new Exercise(6, "Walking", ExerciseCategory.CARDIO));
        exercises.add(new Exercise(7, "Deadlift", ExerciseCategory.STRENGTH));

        builder.setView(view)
                .setTitle("Add an exercise")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        exerciseDialog_iptExercise = view.findViewById(R.id.exerciseDialog_iptExercise);

        String[] exerciseNames = new String[exercises.size()];
        int i = 0;
        for (Exercise exercise : exercises) {
            exerciseNames[i++] = exercise.getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.exercise_dropdown_item, exerciseNames);
        exerciseDialog_iptExercise.setAdapter(adapter);

        exerciseDialog_iptExercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

                ExerciseCategory previousCategory = exerciseSelected != null ? exerciseSelected.getCategory() : null;

                exerciseSelected = exercises.get(pos);

                if (exerciseSelected.getCategory() != ExerciseCategory.CARDIO) {
                    exerciseDialog_btnAddSet.setVisibility(View.VISIBLE);
                } else {
                    exerciseDialog_btnAddSet.setVisibility(View.GONE);
                }

                // clear layout if different category is selected
                if (exerciseSelected.getCategory() != previousCategory) {
                    exerciseDialog_setsLayout.removeAllViews();
                    setCount = 0;
                }

                switch (exerciseSelected.getCategory()) {
                    case BODYWEIGHT:
                        setupBodyweight();
                        break;

                    case STRENGTH:
                        setupStrength();
                        break;

                    case CARDIO:
                        setupCardio();
                        break;
                }

                exerciseDialog_iptExerciseLayout.setError(null);
                exerciseDialog_iptExerciseLayout.setErrorEnabled(false);
            }
        });

        exerciseDialog_setsLayout = view.findViewById(R.id.exerciseDialog_setsLayout);
        exerciseDialog_btnAddSet = view.findViewById(R.id.exerciseDialog_btnAddSet);
        exerciseDialog_setsLayoutScroll = view.findViewById(R.id.exerciseDialog_setsLayoutScroll);
        exerciseDialog_iptExerciseLayout = view.findViewById(R.id.exerciseDialog_iptExerciseLayout);

        exerciseDialog_btnAddSet.setVisibility(View.GONE);

        exerciseDialog_btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSetLineInput();

                exerciseDialog_setsLayoutScroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exerciseDialog_setsLayoutScroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 100);
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!validateInputs()) {
                            return;
                        }

                        boolean noSets = (exerciseSelected.getCategory() == ExerciseCategory.CARDIO) ||
                                (exerciseSelected.getCategory() == ExerciseCategory.STRETCHING);

                        List<SetPerformed> setsPerformed = new ArrayList<>();

                        int setsCount = exerciseDialog_setsLayout.getChildCount();

                        for (int set = 0; set < setsCount; set++) {

                            SetPerformed setPerformed = null;
                            View viewSet = exerciseDialog_setsLayout.getChildAt(set);

                            switch (exerciseSelected.getCategory()) {
                                case BODYWEIGHT:
                                    // reps
                                    EditText setLineInput_iptReps = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptReps)).getEditText();
                                    Integer repetitions = Integer.parseInt(setLineInput_iptReps.getText().toString());

                                    setPerformed = SetPerformed.bodyweight(repetitions);
                                    break;

                                case STRENGTH:
                                    // reps
                                    EditText setLineInput_iptReps2 = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptReps)).getEditText();
                                    Integer repetitions2 = Integer.parseInt(setLineInput_iptReps2.getText().toString());

                                    EditText setLineInput_iptWeight = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptWeight)).getEditText();
                                    Double weight = Double.parseDouble(setLineInput_iptWeight.getText().toString());

                                    // todo: weightunit
                                    setPerformed = SetPerformed.strength(repetitions2, weight, WeightUnit.KILOGRAM);
                                    break;

                                case CARDIO:
                                    EditText setLineInput_iptTimeHours = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeHours)).getEditText();
                                    int hours = Integer.parseInt(setLineInput_iptTimeHours.getText().toString());

                                    EditText setLineInput_iptTimeMinutes = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeMinutes)).getEditText();
                                    int minutes = Integer.parseInt(setLineInput_iptTimeMinutes.getText().toString());

                                    EditText setLineInput_iptTimeSeconds = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeSeconds)).getEditText();
                                    int seconds = Integer.parseInt(setLineInput_iptTimeSeconds.getText().toString());

                                    setPerformed = SetPerformed.cardio(hours * 3600 + minutes * 60 + seconds);
                                    break;

                                case STRETCHING:
                                    setPerformed = SetPerformed.stretching();
                                    break;
                            }

                            setsPerformed.add(setPerformed);
                        }

                        ExercisePerformed exercisePerformed = new ExercisePerformed(exerciseSelected,
                                setsPerformed, noSets);

                        listener.applyExercise(exercisePerformed);
                        dialog.dismiss();
                    }
                });
            }
        });


        return dialog;
    }

    private void resetError(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    private boolean validateInputs() {

        if (exerciseSelected == null) {
            exerciseDialog_iptExerciseLayout.setError("Select an exercise");
            return false;
        } else {
            resetError(exerciseDialog_iptExerciseLayout);
        }

        int setsCount = exerciseDialog_setsLayout.getChildCount();
        boolean isValid = true;

        for (int set = 0; set < setsCount; set++) {

            View viewSet = exerciseDialog_setsLayout.getChildAt(set);

            switch (exerciseSelected.getCategory()) {

                case BODYWEIGHT:
                    // reps
                    EditText setLineInput_iptReps = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptReps)).getEditText();
                    String repetitionsStr = setLineInput_iptReps.getText().toString();
                    if (repetitionsStr.isEmpty()) {
                        setLineInput_iptReps.setError("Enter a value");
                        isValid = false;
                    }

                    if (!NumericHelper.isInteger(repetitionsStr)) {
                        setLineInput_iptReps.setError("Value not a number");
                        isValid = false;
                    }

                    if (!isValid) {
                        resetError((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptReps));
                    }

                    break;

                case STRENGTH:
                    // reps
                    EditText setLineInput_iptReps2 = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptReps)).getEditText();
                    String repetitionsStr2 = setLineInput_iptReps2.getText().toString();

                    if (repetitionsStr2.isEmpty()) {
                        setLineInput_iptReps2.setError("Enter a value");
                        isValid = false;
                    }

                    if (!NumericHelper.isInteger(repetitionsStr2)) {
                        setLineInput_iptReps2.setError("Value not a number");
                        isValid = false;
                    }

                    EditText setLineInput_iptWeight = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptWeight)).getEditText();
                    String weightStr = setLineInput_iptWeight.getText().toString();

                    if (weightStr.isEmpty()) {
                        setLineInput_iptWeight.setError("Enter a value");
                        isValid = false;
                    }

                    if (!NumericHelper.isDouble(weightStr)) {
                        setLineInput_iptWeight.setError("Value not a number");
                        isValid = false;
                    }

                    if (!isValid) {
                        resetError((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptReps));
                        resetError((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptWeight));
                    }

                    break;

                case CARDIO:

                    // hours
                    EditText setLineInput_iptTimeHours = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeHours)).getEditText();
                    String hoursStr = setLineInput_iptTimeHours.getText().toString();

                    if (hoursStr.isEmpty()) {
                        setLineInput_iptTimeHours.setError("Enter a value");
                        isValid = false;
                    }

                    if (!NumericHelper.isInteger(hoursStr)) {
                        setLineInput_iptTimeHours.setError("Value not a number");
                        isValid = false;
                    }

                    // minutes
                    EditText setLineInput_iptTimeMinutes = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeMinutes)).getEditText();
                    String minutesStr = setLineInput_iptTimeHours.getText().toString();

                    if (minutesStr.isEmpty()) {
                        setLineInput_iptTimeMinutes.setError("Enter a value");
                        isValid = false;
                    }

                    if (!NumericHelper.isInteger(minutesStr)) {
                        setLineInput_iptTimeMinutes.setError("Value not a number");
                        isValid = false;
                    }

                    // seconds
                    EditText setLineInput_iptTimeSeconds = ((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeSeconds)).getEditText();
                    String secondsStr = setLineInput_iptTimeHours.getText().toString();

                    if (secondsStr.isEmpty()) {
                        setLineInput_iptTimeSeconds.setError("Enter a value");
                        isValid = false;
                    }

                    if (!NumericHelper.isInteger(secondsStr)) {
                        setLineInput_iptTimeSeconds.setError("Value not a number");
                        isValid = false;
                    }

                    if (!isValid) {
                        resetError((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeHours));
                        resetError((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeMinutes));
                        resetError((TextInputLayout) viewSet.findViewById(R.id.setLineInput_iptTimeSeconds));
                    }

                    break;
            }
        }

        return isValid;
    }

    private void setupBodyweight() {
        setLineInputViewId = R.layout.set_line_input_bodyweight;
        addSetLineInput();
    }

    private void setupStrength() {
        setLineInputViewId = R.layout.set_line_input_strength;
        addSetLineInput();
    }

    private void setupCardio() {
        setLineInputViewId = R.layout.set_line_input_cardio;
        addSetLineInput();
    }

    private void addSetLineInput() {

        if (setLineInputViewId == null) {
            return;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(setLineInputViewId, null);
        exerciseDialog_setsLayout.addView(v);

        if (exerciseSelected.getCategory() != ExerciseCategory.CARDIO) {
            TextView setLineInput_txtSet = v.findViewById(R.id.setLineInput_txtSet);
            setLineInput_txtSet.setText("Set " + (setCount + 1));

            ImageView setLineInput_btnDelete = v.findViewById(R.id.setLineInput_btnDelete);
            setLineInput_btnDelete.setTag(setCount);
            setLineInput_btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int indexToDelete = (int) v.getTag();

                    int childCount = exerciseDialog_setsLayout.getChildCount();

                    for (int i = indexToDelete + 1; i < childCount; i++) {
                        View toModify = exerciseDialog_setsLayout.getChildAt(i);
                        TextView setLineInput_txtSet = toModify.findViewById(R.id.setLineInput_txtSet);
                        ImageView setLineInput_btnDelete = toModify.findViewById(R.id.setLineInput_btnDelete);
                        setLineInput_txtSet.setText("Set " + i);
                        setLineInput_btnDelete.setTag((i - 1));
                    }

                    setCount = childCount - 1;
                    exerciseDialog_setsLayout.removeViewAt(indexToDelete);
                }
            });

            setCount++;
        }
    }

    public interface NewExerciseDialogListener {
        void applyExercise(ExercisePerformed exercisePerformed);
    }
}
