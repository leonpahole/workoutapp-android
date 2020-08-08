package com.leonpahole.workoutapplication;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class LiveCardioFragment extends Fragment implements SetFragment {

    TextView liveCardio_txtName, liveCardio_txtTimer;
    Button liveCardio_btnStartPause, liveCardio_btnEndExercise;

    LiveWorkoutActivity.CurrentSetData currentSetData = null;
    Exercise exercise = null;

    TimeDescriptor cardioTime;

    Handler cardioTimer;
    boolean cardioTimerRunning = false;

    int timeElapsed = 0;

    enum Mode {
        STOPWATCH,
        COUNTDOWN
    }

    Mode timerMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.exercise = GsonUtil.getGsonParser().fromJson(getArguments().getString("exercise"), Exercise.class);
        this.currentSetData = GsonUtil.getGsonParser().fromJson(getArguments().getString("setData"), LiveWorkoutActivity.CurrentSetData.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_cardio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        liveCardio_txtName = view.findViewById(R.id.liveCardio_txtName);
        liveCardio_txtTimer = view.findViewById(R.id.liveCardio_txtTimer);
        liveCardio_btnStartPause = view.findViewById(R.id.liveCardio_btnStartPause);
        liveCardio_btnEndExercise = view.findViewById(R.id.liveCardio_btnEndExercise);

        liveCardio_txtName.setText(exercise.getName());
        
        setupTimer();
        btnStartPause();
        btnEndExercise();
    }

    private void btnEndExercise() {
        liveCardio_btnEndExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerMode == Mode.COUNTDOWN && cardioTime.getSeconds() == 0) {

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
                } else {
                    endExercise();
                }
            }
        });
    }

    public void endExercise() {
        LiveWorkoutActivity activity = (LiveWorkoutActivity) getActivity();
        SetPerformed setPerformed = SetPerformed.cardio(TimeDescriptor.fromSeconds(timeElapsed));
        List<SetPerformed> setsPerformed = new ArrayList<>();
        setsPerformed.add(setPerformed);

        activity.onExerciseEnd(exercise, setsPerformed);
    }

    private void btnStartPause() {
        liveCardio_btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardioTimerRunning = !cardioTimerRunning;

                if (cardioTimerRunning) {
                    liveCardio_btnStartPause.setText("Pause");
                } else {
                    liveCardio_btnStartPause.setText("Resume");
                }
            }
        });
    }

    private void setupTimer() {
        if (currentSetData.getCardioTime() == null) {
            cardioTime = new TimeDescriptor(0, 0, 0);
            timerMode = Mode.STOPWATCH;
        } else {
            cardioTime = new TimeDescriptor(currentSetData.getCardioTime().getSeconds(),
                    currentSetData.getCardioTime().getMinutes(),
                    currentSetData.getCardioTime().getHours());
            timerMode = Mode.COUNTDOWN;
        }

        setRestTimeUI(cardioTime);

        cardioTimer = new Handler();
        cardioTimer.post(new Runnable() {
            @Override
            public void run() {
                if (cardioTimerRunning) {
                    if (timerMode == Mode.COUNTDOWN) {
                        cardioTime.subtractSecond();
                    } else if (timerMode == Mode.STOPWATCH) {
                        cardioTime.addSecond();
                    }

                    timeElapsed++;
                    setRestTimeUI(cardioTime);

                    if (timerMode == Mode.COUNTDOWN && cardioTime.getSeconds() == 0) {
                        liveCardio_btnStartPause.setVisibility(View.GONE);
                        cardioTimerRunning = false;
                        return;
                    }
                }

                cardioTimer.postDelayed(this, 1000);
            }
        });
    }

    private void setRestTimeUI(TimeDescriptor time) {
        liveCardio_txtTimer.setText(
                String.format("%02d", time.getHours()) + ":" +
                        String.format("%02d", time.getMinutes()) + ":" +
                        String.format("%02d", time.getSeconds()));
    }
}