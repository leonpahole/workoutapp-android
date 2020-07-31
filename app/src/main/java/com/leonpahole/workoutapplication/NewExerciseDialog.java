package com.leonpahole.workoutapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class NewExerciseDialog extends DialogFragment {

    private TextInputLayout exerciseDialog_iptExerciseLayout;
    private AutoCompleteTextView exerciseDialog_iptExercise;
    private List<Exercise> exercises;
    private Exercise exerciseSelected;

    private NewExerciseDialogListener listener;

    LinearLayout exerciseDialog_setsLayout;
    Button exerciseDialog_btnAddSet;
    ScrollView exerciseDialog_setsLayoutScroll;

    public void setListener(NewExerciseDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_exercise_dialog, null);

        exercises = LocalStorage.getExercises(getContext());

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

        ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(getContext(), R.layout.exercise_dropdown_item, exercises);
        exerciseDialog_iptExercise.setAdapter(adapter);

        exerciseDialog_iptExercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

                ExerciseCategory previousCategory = exerciseSelected != null ? exerciseSelected.getCategory() : null;

                exerciseSelected = exercises.get(index);

                if (exerciseSelected.getCategory() == ExerciseCategory.STRETCHING ||
                        exerciseSelected.getCategory() == ExerciseCategory.CARDIO) {
                    exerciseDialog_btnAddSet.setVisibility(View.GONE);
                } else {
                    exerciseDialog_btnAddSet.setVisibility(View.VISIBLE);
                }

                // clear layout if different category is selected
                if (exerciseSelected.getCategory() != previousCategory) {
                    exerciseDialog_setsLayout.removeAllViews();

                    if (exerciseSelected.getCategory() != ExerciseCategory.STRETCHING) {
                        addSetLineInput(null);
                    }
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
                addSetLineInput(null);

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

                        List<SetPerformed> setsPerformed = new ArrayList<>();

                        int setsCount = exerciseDialog_setsLayout.getChildCount();

                        for (int set = 0; set < setsCount; set++) {

                            SetPerformed setPerformed = null;
                            View viewSet = exerciseDialog_setsLayout.getChildAt(set);

                            switch (exerciseSelected.getCategory()) {
                                case BODYWEIGHT:
                                    SetLineStrength setLineBodyweight = (SetLineStrength) viewSet;
                                    setPerformed = SetPerformed.bodyweight(setLineBodyweight.getReps());
                                    break;

                                case STRENGTH:
                                    SetLineStrength setLineStrength = (SetLineStrength) viewSet;
                                    setPerformed = SetPerformed.strength(setLineStrength.getReps(), setLineStrength.getWeight());
                                    break;

                                case CARDIO:
                                    SetLineCardio setLineCardio = (SetLineCardio) viewSet;
                                    setPerformed = SetPerformed.cardio(setLineCardio.getTime());
                                    break;

                                case STRETCHING:
                                    setPerformed = SetPerformed.stretching();
                                    break;
                            }

                            setsPerformed.add(setPerformed);
                        }

                        ExercisePerformed exercisePerformed = new ExercisePerformed(exerciseSelected,
                                setsPerformed);

                        listener.applyExercise(exercisePerformed);
                        dialog.dismiss();
                    }
                });
            }
        });

        Bundle arguments = getArguments();

        if (arguments != null) {

            String exerciseToEdit_String = arguments.getString("exercisePerformed", null);

            if (exerciseToEdit_String != null) {

                ExercisePerformed exercisePerformed = GsonUtil.getGsonParser().fromJson(exerciseToEdit_String, ExercisePerformed.class);
                this.exerciseSelected = exercisePerformed.getExercise();

                exerciseDialog_iptExercise.setText(exerciseSelected.getName());

                if (exerciseSelected.getCategory() != ExerciseCategory.STRETCHING) {
                    exerciseDialog_btnAddSet.setVisibility(View.VISIBLE);
                } else {
                    exerciseDialog_btnAddSet.setVisibility(View.GONE);
                }

                List<SetPerformed> setsPerformed = exercisePerformed.getSets();
                for (SetPerformed set : setsPerformed) {
                    addSetLineInput(set);
                }
            }
        }

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

                case STRENGTH:
                case BODYWEIGHT:

                    SetLineStrength setLineStrength = (SetLineStrength) viewSet;
                    isValid = setLineStrength.validate();

                    break;
            }
        }

        return isValid;
    }

    private void addSetLineInput(SetPerformed set) {

        if (exerciseSelected == null) {
            return;
        }

        if (exerciseSelected.getCategory() == ExerciseCategory.STRETCHING) {
            return;
        }

        if (exerciseSelected.getCategory() == ExerciseCategory.STRENGTH ||
                exerciseSelected.getCategory() == ExerciseCategory.BODYWEIGHT) {

            SetLineStrength setLineStrength = new SetLineStrength(getContext());
            setLineStrength.setSetNumber(exerciseDialog_setsLayout.getChildCount() + 1);
            setLineStrength.setDeleteListener(new SetLineOnClickListener() {
                @Override
                public void onClick(View view, int setNumber) {

                    int position = setNumber - 1;

                    int childCount = exerciseDialog_setsLayout.getChildCount();

                    for (int i = position + 1; i < childCount; i++) {
                        SetLineStrength toModify = (SetLineStrength) exerciseDialog_setsLayout.getChildAt(i);
                        toModify.setSetNumber(i);
                    }

                    exerciseDialog_setsLayout.removeViewAt(position);
                }
            });

            if (exerciseSelected.getCategory() == ExerciseCategory.BODYWEIGHT) {
                setLineStrength.setBodyweight(true);
            }


            if (set != null) {
                setLineStrength.setReps(set.getRepetitions());
                setLineStrength.setWeight(set.getWeight());
            }

            exerciseDialog_setsLayout.addView(setLineStrength);
        } else {
            SetLineCardio setLineCardio = new SetLineCardio(getContext());
            exerciseDialog_setsLayout.addView(setLineCardio);

            if (set != null) {
                setLineCardio.setHours(set.getTime().getHours());
                setLineCardio.setMinutes(set.getTime().getMinutes());
                setLineCardio.setSeconds(set.getTime().getSeconds());
            }
        }
    }

    public interface NewExerciseDialogListener {
        void applyExercise(ExercisePerformed exercisePerformed);
    }
}
