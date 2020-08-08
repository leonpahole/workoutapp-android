package com.leonpahole.workoutapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.leonpahole.workoutapplication.utils.LocalStorage;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DashboardHomeFragment extends Fragment {

    Button dashboardHome_btnStartWorkout, dashboardHome_btnLogWorkout, dashboardHome_btnWorkoutHistory,
            dashboardHome_btnExerciseList;
    TextView dashboardHome_txtGreeting, dashboardHome_txtGreetingDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dashboardHome_btnLogWorkout = view.findViewById(R.id.dashboardHome_btnLogWorkout);
        dashboardHome_btnStartWorkout = view.findViewById(R.id.dashboardHome_btnStartWorkout);
        dashboardHome_btnWorkoutHistory = view.findViewById(R.id.dashboardHome_btnWorkoutHistory);
        dashboardHome_btnExerciseList = view.findViewById(R.id.dashboardHome_btnExerciseList);

        dashboardHome_txtGreeting = view.findViewById(R.id.dashboardHome_txtGreeting);
        dashboardHome_txtGreetingDescription = view.findViewById(R.id.dashboardHome_txtGreetingDescription);

        greetings();

        dashboardHome_btnLogWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).inflateLayoutFragment(LogWorkoutFragment.class);
                    ((DashboardActivity) getActivity()).selectMenuItem(R.id.nav_btnLogWorkout);
                }
            }
        });

        dashboardHome_btnStartWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof DashboardActivity) {
                    Intent intent = new Intent(getContext(), LiveWorkoutActivity.class);
                    startActivity(intent);
                }
            }
        });

        dashboardHome_btnWorkoutHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).inflateLayoutFragment(WorkoutHistoryFragment.class);
                    ((DashboardActivity) getActivity()).selectMenuItem(R.id.nav_btnWorkoutHistory);
                }
            }
        });

        dashboardHome_btnExerciseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).inflateLayoutFragment(ExerciseListFragment.class);
                    ((DashboardActivity) getActivity()).selectMenuItem(R.id.nav_btnBrowseExercises);
                }
            }
        });
    }

    private void greetings() {

        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        String greeting;
        String greetingDescription;
        if (currentHour >= 4 && currentHour <= 10) {
            greeting = "Good morning";
            greetingDescription = "We hope you have a productive day.";
        } else if (currentHour >= 11 && currentHour <= 17) {
            greeting = "Hello";
            greetingDescription = "We hope your day is going well.";
        } else if (currentHour >= 18 && currentHour <= 22) {
            greeting = "Good evening";
            greetingDescription = "We hope you are relaxing after a good day.";
        } else {
            greeting = "Good night";
            greetingDescription = "We hope your day was amazing.";
        }

        dashboardHome_txtGreeting.setText(greeting + ", " + LocalStorage.getUser(getContext()).getName());
        dashboardHome_txtGreetingDescription.setText(greetingDescription);
    }
}