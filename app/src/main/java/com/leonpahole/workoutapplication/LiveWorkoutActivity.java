package com.leonpahole.workoutapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LiveWorkoutActivity extends AppCompatActivity implements SelectExerciseFragment.OnExerciseSelectListener {

    Button liveWorkout_btnEndWorkout;
    ImageView liveWorkout_imgClose;

    Workout workout;
    CurrentSetData currentSetData = null;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class CurrentSetData {
        TimeDescriptor restTime;
        TimeDescriptor cardioTime;
        TimeDescriptor timedTime;
        Double weight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_workout);

        liveWorkout_btnEndWorkout = findViewById(R.id.liveWorkout_btnEndWorkout);
        liveWorkout_imgClose = findViewById(R.id.liveWorkout_imgClose);

        workout = new Workout();

        workout.setExercisesPerformed(new ArrayList<ExercisePerformed>());
        workout.setStartDate(new SimpleDateFormat("dd. MM. yyyy").format(new Date()));
        workout.setStartTime(new SimpleDateFormat("HH:mm").format(new Date()));

        showExercisePickerFragment();

        liveWorkout_btnEndWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LiveWorkoutActivity.this);
                builder.setCancelable(true);
                builder.setTitle("End workout?");
                builder.setMessage("Do you really want to end the workout and log it?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        liveWorkout_btnEndWorkout.setVisibility(View.GONE);

                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.liveWorkout_frameLayout);
                        if (SetFragment.class.isInstance(currentFragment)) {
                            ((SetFragment) currentFragment).endExercise();
                        }

                        Bundle bundle = new Bundle();
                        workout.setEndTime(new SimpleDateFormat("HH:mm").format(new Date()));
                        bundle.putString("workout", GsonUtil.getGsonParser().toJson(workout));
                        inflateLayoutFragment(LogWorkoutFragment.class, bundle);
                    }
                });

                builder.setNegativeButton("No", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        liveWorkout_imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
    }

    private void showExercisePickerFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("exerciseNumber", workout.getExercisesPerformed().size() + 1);
        inflateLayoutFragment(SelectExerciseFragment.class, bundle);
    }

    private void closeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LiveWorkoutActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Cancel workout?");
        builder.setMessage("Do you really want to cancel the workout and discard it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        closeDialog();
    }

    public void inflateLayoutFragment(Class fragmentClass, Bundle arguments) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            if (arguments != null) {
                fragment.setArguments(arguments);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.liveWorkout_frameLayout, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExerciseSelect(Exercise exercise, CurrentSetData currentSetData) {

        this.currentSetData = currentSetData;

        Bundle bundle = new Bundle();
        bundle.putString("exercise", GsonUtil.getGsonParser().toJson(exercise));
        bundle.putString("setData", GsonUtil.getGsonParser().toJson(currentSetData));

        if (exercise.getCategory() == ExerciseCategory.STRENGTH ||
                exercise.getCategory() == ExerciseCategory.BODYWEIGHT) {
            inflateLayoutFragment(LiveSetFragment.class, bundle);
        } else if (exercise.getCategory() == ExerciseCategory.TIMED ||
                exercise.getCategory() == ExerciseCategory.WEIGHTED_TIMED) {
            inflateLayoutFragment(TimedSetFragment.class, bundle);
        } else if (exercise.getCategory() == ExerciseCategory.CARDIO) {
            inflateLayoutFragment(LiveCardioFragment.class, bundle);
        } else if (exercise.getCategory() == ExerciseCategory.STRETCHING) {
            onExerciseEnd(exercise, new ArrayList<SetPerformed>());
        }
    }

    public void onExerciseEnd(Exercise exercise, List<SetPerformed> setsPerformed) {
        workout.getExercisesPerformed().add(new ExercisePerformed(exercise, setsPerformed));
        showExercisePickerFragment();
    }
}