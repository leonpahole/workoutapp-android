package com.leonpahole.workoutapplication;

import android.app.Activity;
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

public class DashboardHomeFragment extends Fragment {

    Button dashboardHome_btnStartWorkout, dashboardHome_btnLogWorkout;

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
                    ((DashboardActivity) getActivity()).inflateLayoutFragment(LiveWorkoutFragment.class);
                    ((DashboardActivity) getActivity()).selectMenuItem(R.id.nav_btnNewWorkout);
                }
            }
        });
    }
}