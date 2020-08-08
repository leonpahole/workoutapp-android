package com.leonpahole.workoutapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

import java.util.ArrayList;
import java.util.Set;

public class SelectExerciseFragment extends Fragment {

    public interface OnExerciseSelectListener {
        public void onExerciseSelect(Exercise exercise, LiveWorkoutActivity.CurrentSetData currentSetData);
    }

    OnExerciseSelectListener onExerciseSelectListener;

    TextView liveWorkoutSelect_txtSelectExercise;
    TextInputLayout liveWorkoutSelect_iptExerciseLayout, liveWorkoutSelect_iptWeight;
    CheckBox liveWorkoutSelect_chkCountdownRest, liveWorkoutSelect_chkCountdownCardioTime,
            liveWorkoutSelect_chkTimedCountdown;
    AutoCompleteTextView liveWorkoutSelect_iptExercise;
    Button liveWorkoutSelect_btnSelect;
    DurationPicker liveWorkout_restDurationPicker, liveWorkout_cardioDurationPicker,
            liveWorkoutSelect_timedDurationPicker;

    View liveWorkoutSelect_layoutStrengthBodyweight, liveWorkoutSelect_layoutCardio,
            liveWorkout_layoutRestTime, liveWorkout_layoutCardioTime, liveWorkoutSelect_layoutTimed,
            liveWorkoutSelect_layoutTimedTime;

    private ArrayList<Exercise> exercises;
    Exercise exerciseSelected = null;

    int exerciseNumber;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onExerciseSelectListener = (OnExerciseSelectListener) ((Activity) context);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnExerciseSelectListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            exerciseNumber = arguments.getInt("exerciseNumber");
        } else {
            exerciseNumber = 1;
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        liveWorkoutSelect_txtSelectExercise = view.findViewById(R.id.liveWorkoutSelect_txtSelectExercise);

        liveWorkoutSelect_txtSelectExercise.setText("Select exercise #" + exerciseNumber);

        liveWorkoutSelect_iptExerciseLayout = view.findViewById(R.id.liveWorkoutSelect_iptExerciseLayout);
        liveWorkoutSelect_iptWeight = view.findViewById(R.id.liveWorkoutSelect_iptWeight);

        liveWorkoutSelect_chkCountdownRest = view.findViewById(R.id.liveWorkoutSelect_chkCountdownRest);
        liveWorkoutSelect_chkCountdownCardioTime = view.findViewById(R.id.liveWorkoutSelect_chkCountdownCardioTime);
        liveWorkoutSelect_chkTimedCountdown = view.findViewById(R.id.liveWorkoutSelect_chkTimedCountdown);
        liveWorkoutSelect_layoutTimedTime = view.findViewById(R.id.liveWorkoutSelect_layoutTimedTime);

        liveWorkoutSelect_timedDurationPicker = view.findViewById(R.id.liveWorkoutSelect_timedDurationPicker);

        liveWorkoutSelect_iptExercise = view.findViewById(R.id.liveWorkoutSelect_iptExercise);

        liveWorkoutSelect_btnSelect = view.findViewById(R.id.liveWorkoutSelect_btnSelect);

        liveWorkoutSelect_layoutStrengthBodyweight = view.findViewById(R.id.liveWorkoutSelect_layoutStrengthBodyweight);
        liveWorkoutSelect_layoutCardio = view.findViewById(R.id.liveWorkoutSelect_layoutCardio);
        liveWorkout_layoutRestTime = view.findViewById(R.id.liveWorkout_layoutRestTime);
        liveWorkout_layoutCardioTime = view.findViewById(R.id.liveWorkout_layoutCardioTime);
        liveWorkoutSelect_layoutTimed = view.findViewById(R.id.liveWorkoutSelect_layoutTimed);

        liveWorkout_restDurationPicker = view.findViewById(R.id.liveWorkout_restDurationPicker);
        liveWorkout_cardioDurationPicker = view.findViewById(R.id.liveWorkout_cardioDurationPicker);

        liveWorkoutSelect_layoutStrengthBodyweight.setVisibility(View.GONE);

        exercises = LocalStorage.getExercises(getContext());

        autocompleteExercise();
        setupCheckboxes();

        liveWorkoutSelect_btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exerciseSelected == null) {
                    return;
                }

                LiveWorkoutActivity.CurrentSetData currentSetData = new LiveWorkoutActivity.CurrentSetData();

                switch (exerciseSelected.getCategory()) {
                    case BODYWEIGHT:
                    case STRENGTH:

                        if (exerciseSelected.getCategory() == ExerciseCategory.STRENGTH) {

                            String weightStr = liveWorkoutSelect_iptWeight.getEditText().getText().toString().trim();

                            if (!weightStr.isEmpty()) {
                                currentSetData.setWeight(Double.parseDouble(weightStr));
                            }
                        }

                        if (liveWorkoutSelect_chkCountdownRest.isChecked()) {

                            TimeDescriptor restDuration = new TimeDescriptor(liveWorkout_restDurationPicker.getSeconds(),
                                    liveWorkout_restDurationPicker.getMinutes(),
                                    liveWorkout_restDurationPicker.getHours());

                            if (restDuration.toSeconds() > 0) {
                                currentSetData.setRestTime(restDuration);
                            }
                        }


                        break;

                    case CARDIO:

                        if (liveWorkoutSelect_chkCountdownCardioTime.isChecked()) {

                            TimeDescriptor cardioTime = new TimeDescriptor(liveWorkout_cardioDurationPicker.getSeconds(),
                                    liveWorkout_cardioDurationPicker.getMinutes(),
                                    liveWorkout_cardioDurationPicker.getHours());

                            if (cardioTime.toSeconds() > 0) {
                                currentSetData.setCardioTime(cardioTime);
                            }
                        }
                        break;

                    case WEIGHTED_TIMED:
                    case TIMED:
                        if (exerciseSelected.getCategory() == ExerciseCategory.WEIGHTED_TIMED) {

                            String weightStr = liveWorkoutSelect_iptWeight.getEditText().getText().toString().trim();

                            if (!weightStr.isEmpty()) {
                                currentSetData.setWeight(Double.parseDouble(weightStr));
                            }
                        }

                        if (liveWorkoutSelect_chkTimedCountdown.isChecked()) {

                            TimeDescriptor timeDuration = new TimeDescriptor(liveWorkoutSelect_timedDurationPicker.getSeconds(),
                                    liveWorkoutSelect_timedDurationPicker.getMinutes(),
                                    liveWorkoutSelect_timedDurationPicker.getHours());

                            if (timeDuration.toSeconds() > 0) {
                                currentSetData.setTimedTime(timeDuration);
                            }
                        }
                }

                onExerciseSelectListener.onExerciseSelect(exerciseSelected, currentSetData);
            }
        });
    }

    private void autocompleteExercise() {

        ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(getContext(), R.layout.exercise_dropdown_item, exercises);
        liveWorkoutSelect_iptExercise.setAdapter(adapter);

        liveWorkoutSelect_iptExercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

                if (exerciseSelected.getCategory() != previousCategory) {
                    hideEverything();

                    switch (exerciseSelected.getCategory()) {
                        case BODYWEIGHT:
                            showBodyweight();
                            break;

                        case STRENGTH:
                            showStrength();
                            break;

                        case CARDIO:
                            showCardio();
                            break;

                        case WEIGHTED_TIMED:
                            showWeightedTimed();
                            break;

                        case TIMED:
                            showTimed();
                            break;
                    }
                }

                liveWorkoutSelect_btnSelect.setVisibility(View.VISIBLE);

                liveWorkoutSelect_iptExerciseLayout.setError(null);
                liveWorkoutSelect_iptExerciseLayout.setErrorEnabled(false);
            }
        });
    }

    private void setupCheckboxes() {

        liveWorkoutSelect_chkCountdownCardioTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    liveWorkout_layoutCardioTime.setVisibility(View.VISIBLE);
                } else {
                    liveWorkout_layoutCardioTime.setVisibility(View.GONE);
                }
            }
        });

        liveWorkoutSelect_chkCountdownRest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    liveWorkout_layoutRestTime.setVisibility(View.VISIBLE);
                } else {
                    liveWorkout_layoutRestTime.setVisibility(View.GONE);
                }
            }
        });

        liveWorkoutSelect_chkTimedCountdown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    liveWorkoutSelect_layoutTimedTime.setVisibility(View.VISIBLE);
                } else {
                    liveWorkoutSelect_layoutTimedTime.setVisibility(View.GONE);
                }
            }
        });
    }

    private void hideEverything() {
        liveWorkoutSelect_layoutStrengthBodyweight.setVisibility(View.GONE);
        liveWorkoutSelect_iptWeight.setVisibility(View.GONE);
        liveWorkout_layoutRestTime.setVisibility(View.GONE);
        liveWorkoutSelect_layoutTimedTime.setVisibility(View.GONE);
        liveWorkoutSelect_layoutCardio.setVisibility(View.GONE);
        liveWorkout_layoutCardioTime.setVisibility(View.GONE);
        liveWorkoutSelect_layoutTimed.setVisibility(View.GONE);
    }

    private void showCardio() {
        liveWorkoutSelect_layoutCardio.setVisibility(View.VISIBLE);
    }

    private void showStrength() {
        liveWorkoutSelect_layoutStrengthBodyweight.setVisibility(View.VISIBLE);
        liveWorkoutSelect_iptWeight.setVisibility(View.VISIBLE);
    }

    private void showBodyweight() {
        liveWorkoutSelect_layoutStrengthBodyweight.setVisibility(View.VISIBLE);
    }

    private void showWeightedTimed() {
        liveWorkoutSelect_layoutTimed.setVisibility(View.VISIBLE);
        liveWorkoutSelect_iptWeight.setVisibility(View.VISIBLE);
    }

    private void showTimed() {
        liveWorkoutSelect_layoutTimed.setVisibility(View.VISIBLE);
        liveWorkoutSelect_iptWeight.setVisibility(View.GONE);
    }
}