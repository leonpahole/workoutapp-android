package com.leonpahole.workoutapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;
import com.leonpahole.workoutapplication.utils.exercises.TimeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class LiveSetFragment extends Fragment {

    TextView liveSet_txtName, liveSet_txtSetNumber;
    LinearLayout liveSet_layoutTimer, liveSet_layoutFinishSet, liveSet_layoutSetList,
            liveSet_layoutExerciseInfo;
    TextView liveSet_txtTimer;
    ScrollView liveSet_layoutSetListScroll;
    Button liveSet_btnSkipRest, liveSet_btnLogSet, liveSet_btnEndExercise;

    LiveWorkoutActivity.CurrentSetData currentSetData = null;
    Exercise exercise = null;

    CountDownTimer restCountdown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.exercise = GsonUtil.getGsonParser().fromJson(getArguments().getString("exercise"), Exercise.class);
        this.currentSetData = GsonUtil.getGsonParser().fromJson(getArguments().getString("setData"), LiveWorkoutActivity.CurrentSetData.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_set, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        liveSet_txtName = view.findViewById(R.id.liveSet_txtName);
        liveSet_txtSetNumber = view.findViewById(R.id.liveSet_txtSetNumber);
        liveSet_layoutTimer = view.findViewById(R.id.liveSet_layoutTimer);
        liveSet_layoutFinishSet = view.findViewById(R.id.liveSet_layoutFinishSet);
        liveSet_layoutSetList = view.findViewById(R.id.liveSet_layoutSetList);
        liveSet_txtTimer = view.findViewById(R.id.liveSet_txtTimer);
        liveSet_layoutSetListScroll = view.findViewById(R.id.liveSet_layoutSetListScroll);
        liveSet_btnSkipRest = view.findViewById(R.id.liveSet_btnSkipRest);
        liveSet_btnLogSet = view.findViewById(R.id.liveSet_btnLogSet);
        liveSet_btnEndExercise = view.findViewById(R.id.liveSet_btnEndExercise);
        liveSet_layoutExerciseInfo = view.findViewById(R.id.liveSet_layoutExerciseInfo);

        liveSet_txtName.setText(exercise.getName());

        liveSet_layoutTimer.setVisibility(View.GONE);

        newSet();

        liveSet_btnLogSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearKeyboard();
                endSet();
            }
        });

        liveSet_btnSkipRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restCountdown != null) {
                    restCountdown.cancel();
                    restCountdown.onFinish();
                }
            }
        });

        liveSet_btnEndExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveWorkoutActivity activity = (LiveWorkoutActivity) getActivity();

                List<SetPerformed> setsPerformed = new ArrayList<>();

                boolean isValid = true;

                for (int i = 0; i < liveSet_layoutSetList.getChildCount(); i++) {
                    SetLineStrength setLine = (SetLineStrength) liveSet_layoutSetList.getChildAt(i);

                    boolean setIsValid = setLine.validate();

                    if (isValid) {
                        isValid = setIsValid;
                    }

                    setsPerformed.add(setLine.getSetPerformed());
                }

                if (!isValid) {
                    return;
                }

                activity.onExerciseEnd(exercise, setsPerformed);
            }
        });
    }

    private void clearKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    private void newSet() {
        SetLineStrength setLine = new SetLineStrength(getContext());

        setLine.setShowHeader(false);

        if (exercise.getCategory() == ExerciseCategory.STRENGTH) {
            if (currentSetData.getWeight() != null) {
                setLine.setWeight(currentSetData.getWeight());
            }
        } else if (exercise.getCategory() == ExerciseCategory.STRETCHING) {
            setLine.setBodyweight(true);
        }

        onChangeSets();

        liveSet_layoutFinishSet.removeAllViews();
        liveSet_layoutFinishSet.addView(setLine);
    }

    private void endSet() {

        SetLineStrength setLine = (SetLineStrength) liveSet_layoutFinishSet.getChildAt(0);

        if (!setLine.validate()) {
            return;
        }

        SetLineStrength setLineToAdd = new SetLineStrength(getContext());
        setLineToAdd.setWeight(setLine.getWeight());
        setLineToAdd.setReps(setLine.getReps());
        setLineToAdd.setBodyweight(setLine.isBodyweight());
        setLineToAdd.setSetNumber(liveSet_layoutSetList.getChildCount() + 1);
        setLineToAdd.setDeleteListener(new SetLineOnClickListener() {
            @Override
            public void onClick(View view, int setNumber) {
                int position = setNumber - 1;

                int childCount = liveSet_layoutSetList.getChildCount();

                for (int i = position + 1; i < childCount; i++) {
                    SetLineStrength toModify = (SetLineStrength) liveSet_layoutSetList.getChildAt(i);
                    toModify.setSetNumber(i);
                }

                liveSet_layoutSetList.removeViewAt(position);
                onChangeSets();
            }
        });

        liveSet_layoutSetList.addView(setLineToAdd);

        if (currentSetData.getRestTime() != null) {
            startRest();
        } else {
            newSet();
        }
    }

    private void startRest() {

        long ms = currentSetData.getRestTime().toSeconds() * 1000;
        setRestTimeUI(currentSetData.getRestTime());

        liveSet_layoutExerciseInfo.setVisibility(View.GONE);
        liveSet_layoutTimer.setVisibility(View.VISIBLE);

        restCountdown = new CountDownTimer(ms, 1000) {

            public void onTick(long millisUntilFinished) {
                setRestTimeUI(TimeDescriptor.fromMiliseconds(millisUntilFinished));
            }

            public void onFinish() {
                liveSet_layoutTimer.setVisibility(View.GONE);
                liveSet_layoutExerciseInfo.setVisibility(View.VISIBLE);
                restCountdown = null;
                newSet();
            }

        }.start();
    }

    private void onChangeSets() {
        liveSet_txtSetNumber.setText("Performing set " + (liveSet_layoutSetList.getChildCount() + 1));
    }

    private void setRestTimeUI(TimeDescriptor remaining) {
        liveSet_txtTimer.setText(
                String.format("%02d", remaining.getMinutes()) + ":" +
                        String.format("%02d", remaining.getSeconds()));
    }
}