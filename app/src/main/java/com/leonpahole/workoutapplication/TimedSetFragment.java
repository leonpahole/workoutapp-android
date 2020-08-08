package com.leonpahole.workoutapplication;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.NumericHelper;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class TimedSetFragment extends Fragment {

    TextView liveTimed_txtName, liveTimed_txtTimer, liveTimed_txtWeight, liveTimed_txtSetNumber;
    TextInputLayout liveTimed_iptWeight;
    Button liveTimed_btnStartPause, liveTimed_btnLogSet, liveTimed_btnEndExercise;

    ScrollView liveTimed_layoutSetListScroll;
    LinearLayout liveTimed_layoutSetList, liveTimed_containerWeightLayout;

    LiveWorkoutActivity.CurrentSetData currentSetData = null;
    Exercise exercise = null;

    Handler timer;
    boolean timerRunning = false;
    boolean setStarted = false;
    TimeDescriptor setTime;

    Double setWeight;

    int timeElapsed = 0;

    enum Mode {
        STOPWATCH,
        COUNTDOWN
    }

    LiveCardioFragment.Mode timerMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.exercise = GsonUtil.getGsonParser().fromJson(getArguments().getString("exercise"), Exercise.class);
        this.currentSetData = GsonUtil.getGsonParser().fromJson(getArguments().getString("setData"), LiveWorkoutActivity.CurrentSetData.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timed_set, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        liveTimed_txtName = view.findViewById(R.id.liveTimed_txtName);
        liveTimed_txtTimer = view.findViewById(R.id.liveTimed_txtTimer);
        liveTimed_iptWeight = view.findViewById(R.id.liveTimed_iptWeight);
        liveTimed_btnStartPause = view.findViewById(R.id.liveTimed_btnStartPause);
        liveTimed_btnLogSet = view.findViewById(R.id.liveTimed_btnLogSet);
        liveTimed_btnEndExercise = view.findViewById(R.id.liveTimed_btnEndExercise);
        liveTimed_layoutSetListScroll = view.findViewById(R.id.liveTimed_layoutSetListScroll);
        liveTimed_layoutSetList = view.findViewById(R.id.liveTimed_layoutSetList);
        liveTimed_txtSetNumber = view.findViewById(R.id.liveTimed_txtSetNumber);
        liveTimed_containerWeightLayout = view.findViewById(R.id.liveTimed_containerWeightLayout);
        liveTimed_txtWeight = view.findViewById(R.id.liveTimed_txtWeight);

        liveTimed_txtName.setText(exercise.getName());
        if (exercise.getCategory() == ExerciseCategory.TIMED) {
            liveTimed_containerWeightLayout.setVisibility(View.GONE);
        }

        if (currentSetData.getTimedTime() == null) {
            timerMode = LiveCardioFragment.Mode.STOPWATCH;
        } else {
            timerMode = LiveCardioFragment.Mode.COUNTDOWN;
        }

        liveTimed_btnLogSet.setVisibility(View.GONE);

        setSetTime();
        setTimeUI(setTime);
        txtWaitingForSet();
        btnStartPause();
        btnLogSet();
        setupTimer();
        btnEndExercise();
    }

    private void btnEndExercise() {
        liveTimed_btnEndExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("End exercise?");
                builder.setMessage("End exercise " + exercise.getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endExercise();
                    }
                });

                builder.setNegativeButton("No", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void endExercise() {
        LiveWorkoutActivity activity = (LiveWorkoutActivity) getActivity();

        List<SetPerformed> setsPerformed = new ArrayList<>();

        boolean isValid = true;

        for (int i = 0; i < liveTimed_layoutSetList.getChildCount(); i++) {
            SetLineTimed setLine = (SetLineTimed) liveTimed_layoutSetList.getChildAt(i);

            boolean setIsValid = setLine.validate();

            if (!setIsValid) {
                isValid = false;
            } else {
                setsPerformed.add(setLine.getSetPerformed());
            }
        }

        if (!isValid) {
            return;
        }

        activity.onExerciseEnd(exercise, setsPerformed);
    }

    private void setupTimer() {
        timer = new Handler();
        timer.post(new Runnable() {
            @Override
            public void run() {
                System.out.println(timerRunning);
                if (timerRunning) {
                    if (timerMode == LiveCardioFragment.Mode.COUNTDOWN) {
                        setTime.subtractSecond();
                    } else if (timerMode == LiveCardioFragment.Mode.STOPWATCH) {
                        setTime.addSecond();
                    }

                    timeElapsed++;
                    setTimeUI(setTime);

                    if (timerMode == LiveCardioFragment.Mode.COUNTDOWN && setTime.getSeconds() == 0) {
                        liveTimed_btnStartPause.setVisibility(View.GONE);
                        timerRunning = false;
                        txtEndedSet();
                    }
                }

                timer.postDelayed(this, 1000);
            }
        });
    }

    private void setTimeUI(TimeDescriptor time) {
        liveTimed_txtTimer.setText(time.toString());
    }

    private void txtPerformingSet() {
        liveTimed_txtSetNumber.setText("Performing set " + (liveTimed_layoutSetList.getChildCount() + 1));
    }

    private void txtWaitingForSet() {
        liveTimed_txtSetNumber.setText("Waiting for set " + (liveTimed_layoutSetList.getChildCount() + 1));
    }

    private void txtEndedSet() {
        liveTimed_txtSetNumber.setText("Ended set " + (liveTimed_layoutSetList.getChildCount() + 1));
    }

    private void startSet() {
        if (!validateInputs()) {
            return;
        }

        txtPerformingSet();

        if (exercise.getCategory() == ExerciseCategory.WEIGHTED_TIMED) {
            setWeight = Double.parseDouble(liveTimed_iptWeight.getEditText().getText().toString());
        }

        liveTimed_txtWeight.setVisibility(View.VISIBLE);
        liveTimed_iptWeight.setVisibility(View.GONE);

        setSetTime();

        setStarted = true;
        liveTimed_btnLogSet.setVisibility(View.VISIBLE);
        pauseOrResumeSet();
    }

    private void setSetTime() {
        if (currentSetData.getTimedTime() == null) {
            setTime = new TimeDescriptor(0, 0, 0);
        } else {
            setTime = new TimeDescriptor(currentSetData.getTimedTime().getSeconds(),
                    currentSetData.getTimedTime().getMinutes(),
                    currentSetData.getTimedTime().getHours());
        }
    }

    private void pauseOrResumeSet() {
        timerRunning = !timerRunning;
        if (timerRunning) {
            liveTimed_btnStartPause.setText("Pause");
        } else {
            liveTimed_btnStartPause.setText("Resume");
        }
    }

    private void endSet() {

        if (timerMode == LiveCardioFragment.Mode.COUNTDOWN && setTime.toSeconds() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("End set?");
            builder.setMessage("End set before countdown finished?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    endSetConfirm();
                }
            });

            builder.setNegativeButton("No", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            endSetConfirm();
        }
    }

    private void endSetConfirm() {

        setStarted = false;
        liveTimed_btnLogSet.setVisibility(View.GONE);
        liveTimed_btnStartPause.setVisibility(View.VISIBLE);
        liveTimed_btnStartPause.setText("Start");
        setSetTime();
        setTimeUI(setTime);

        timerRunning = false;

        SetLineTimed setLineToAdd = new SetLineTimed(getContext());
        setLineToAdd.setBodyweight(exercise.getCategory() == ExerciseCategory.TIMED);
        setLineToAdd.setWeight(setWeight);
        setLineToAdd.setTime(TimeDescriptor.fromSeconds(timeElapsed));
        setLineToAdd.setSetNumber(liveTimed_layoutSetList.getChildCount() + 1);
        setLineToAdd.setDeleteListener(new SetLineOnClickListener() {
            @Override
            public void onClick(View view, int setNumber) {
                int position = setNumber - 1;

                int childCount = liveTimed_layoutSetList.getChildCount();

                for (int i = position + 1; i < childCount; i++) {
                    SetLineTimed toModify = (SetLineTimed) liveTimed_layoutSetList.getChildAt(i);
                    toModify.setSetNumber(i);
                }

                liveTimed_layoutSetList.removeViewAt(position);
            }
        });

        timeElapsed = 0;

        liveTimed_layoutSetList.addView(setLineToAdd);

        liveTimed_txtWeight.setVisibility(View.GONE);
        liveTimed_iptWeight.setVisibility(View.VISIBLE);
        txtWaitingForSet();
    }

    private void btnStartPause() {
        liveTimed_btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setStarted) {
                    pauseOrResumeSet();
                } else {
                    startSet();
                }
            }
        });
    }

    private void btnLogSet() {
        liveTimed_btnLogSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSet();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (exercise.getCategory() == ExerciseCategory.WEIGHTED_TIMED) {
            String weightStr = liveTimed_iptWeight.getEditText().getText().toString();

            if (weightStr.isEmpty()) {
                liveTimed_iptWeight.setError("Please enter a value");
                isValid = false;
            } else if (!NumericHelper.isDouble(weightStr)) {
                liveTimed_iptWeight.setError("Please enter numeric value");
                isValid = false;
            } else {
                liveTimed_iptWeight.setError(null);
                liveTimed_iptWeight.setErrorEnabled(false);
            }
        }

        return isValid;
    }
}