package com.leonpahole.workoutapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.adapters.ExercisesAdapter;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LogWorkoutFragment extends Fragment implements NewExerciseDialog.NewExerciseDialogListener {

    TextInputLayout logWorkout_iptStartTime, logWorkout_iptEndTime, logWorkout_iptStartDate;
    RecyclerView logWorkout_recExercises;
    RecyclerView.Adapter exercisesAdapter;

    List<ExercisePerformed> exercisesPerformed;

    Button logWorkout_btnAddExercise;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercisesPerformed = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logWorkout_iptStartDate = view.findViewById(R.id.logWorkout_iptStartDate);
        logWorkout_iptEndTime = view.findViewById(R.id.logWorkout_iptEndTime);
        logWorkout_iptStartTime = view.findViewById(R.id.logWorkout_iptStartTime);

        logWorkout_recExercises = view.findViewById(R.id.logWorkout_recExercises);
        logWorkout_btnAddExercise = view.findViewById(R.id.logWorkout_btnAddExercise);

        dateTimePickers();
        exercisesRecycler();
        addExerciseButton();
    }

    private void exercisesRecycler() {
        // logWorkout_recExercises.setHasFixedSize(true);
        logWorkout_recExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // logWorkout_recExercises.setNestedScrollingEnabled(false);

        exercisesAdapter = new ExercisesAdapter(exercisesPerformed);
        logWorkout_recExercises.setAdapter(exercisesAdapter);
    }

    private void addExerciseButton() {
        logWorkout_btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExerciseDialog newExerciseDialog = new NewExerciseDialog();
                newExerciseDialog.show(getFragmentManager(), "New exercise dialog");
                newExerciseDialog.setListener(LogWorkoutFragment.this);
                // addExercise();
                // exercisesAdapter.notifyItemInserted(exercisesPerformed.size() - 1);
            }
        });
    }

    private void addExercise() {
        Exercise exercise = new Exercise(1, "Pull ups", ExerciseCategory.BODYWEIGHT);
        List<SetPerformed> setsPerformed = new ArrayList<>();
        setsPerformed.add(SetPerformed.bodyweight(30));
        setsPerformed.add(SetPerformed.bodyweight(20));
        setsPerformed.add(SetPerformed.bodyweight(10));

        System.out.println(setsPerformed.size());

        exercisesPerformed.add(new ExercisePerformed(exercise, setsPerformed, false));
    }

    private void dateTimePickers() {
        logWorkout_iptStartDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                logWorkout_iptStartDate.getEditText().setText(dayOfMonth + ". " + (monthOfYear + 1) + ". " + year);
                            }

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        logWorkout_iptStartTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                logWorkout_iptStartTime.getEditText().setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        logWorkout_iptEndTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                logWorkout_iptEndTime.getEditText().setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    @Override
    public void applyExercise(ExercisePerformed exercisePerformed) {
        exercisesPerformed.add(exercisePerformed);
        exercisesAdapter.notifyItemInserted(exercisesPerformed.size() - 1);
    }
}