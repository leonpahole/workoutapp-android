package com.leonpahole.workoutapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.adapters.ExerciseListAdapter;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;

import java.util.List;

public class ExerciseListFragment extends Fragment {

    Button exerciseList_btnAddExercise;
    RecyclerView exerciseList_recExercises;

    RecyclerView.Adapter exercisesAdapter;

    String url = "http://192.168.1.68:8080/api/exercise";
    RequestQueue queue;

    List<Exercise> exercises;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queue = Volley.newRequestQueue(getContext());

        exerciseList_btnAddExercise = view.findViewById(R.id.exerciseList_btnAddExercise);
        exerciseList_recExercises = view.findViewById(R.id.exerciseList_recExercises);

        exercises = LocalStorage.getExercises(getContext());

        exercisesRecycler();
    }

    private void exercisesRecycler() {
        exerciseList_recExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        exercisesAdapter = new ExerciseListAdapter(exercises, new RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
        });

        exerciseList_recExercises.setAdapter(exercisesAdapter);
    }
}