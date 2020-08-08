package com.leonpahole.workoutapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutHistoryFragment extends Fragment {

    RecyclerView workoutHistory_recWorkouts;
    RecyclerView.Adapter workoutsAdapter;

    TextView workoutHistory_txtNoWorkouts;

    List<Workout> workouts;

    String url = "http://192.168.1.68:8080/api/workout";
    RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workouts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queue = Volley.newRequestQueue(getContext());

        workoutHistory_recWorkouts = view.findViewById(R.id.workoutHistory_recWorkouts);
        workoutHistory_txtNoWorkouts = view.findViewById(R.id.workoutHistory_txtNoWorkouts);

        workoutsRecycler();
        fetchWorkouts();
    }

    private void workoutsRecycler() {
        // logWorkout_recExercises.setHasFixedSize(true);
        workoutHistory_recWorkouts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // logWorkout_recExercises.setNestedScrollingEnabled(false);

        workoutsAdapter = new WorkoutListAdapter(workouts, new RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, int position) {
                System.out.println("Clicked " + workouts.get(position).getName());
                Intent intent = new Intent(getContext(), WorkoutDetailsActivity.class);
                intent.putExtra("workoutId", workouts.get(position).getId());
                startActivity(intent);
            }
        });

        workoutHistory_recWorkouts.setAdapter(workoutsAdapter);
    }

    private void fetchWorkouts() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, new JSONArray(), new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        workouts.addAll(Workout.fromJson(getContext(), response));
                        workoutsAdapter.notifyItemRangeChanged(0, workouts.size());

                        if (workouts.isEmpty()) {
                            workoutHistory_txtNoWorkouts.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if (error == null) {
                            Toast.makeText(getContext(), "An unknown error has occured", Toast.LENGTH_LONG).show();
                            return;
                        }

                        error.printStackTrace();

                        if (error.networkResponse == null) {
                            Toast.makeText(getContext(), "A network error has occured", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Unknown application error has occured", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + LocalStorage.getJwt(getContext()));
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }
}