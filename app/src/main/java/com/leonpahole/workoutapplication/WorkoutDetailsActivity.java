package com.leonpahole.workoutapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.adapters.ExercisesAdapter;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutDetailsActivity extends AppCompatActivity {

    String url = "http://192.168.1.68:8080/api/workout";
    RequestQueue queue;
    Workout workout;

    View workoutDetails_rootContainer, workoutDetails_commentsContainer;
    TextView workoutDetails_name, workoutDetails_date, workoutDetails_time, workoutDetails_comments,
            workoutDetails_txtNoExercises;
    ImageView workoutDetails_imgBack;

    RecyclerView workoutDetails_recViewExercises;
    RecyclerView.Adapter exercisesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        workoutDetails_rootContainer = findViewById(R.id.workoutDetails_rootContainer);
        workoutDetails_commentsContainer = findViewById(R.id.workoutDetails_commentsContainer);

        workoutDetails_name = findViewById(R.id.workoutDetails_name);
        workoutDetails_date = findViewById(R.id.workoutDetails_date);
        workoutDetails_time = findViewById(R.id.workoutDetails_time);
        workoutDetails_comments = findViewById(R.id.workoutDetails_comments);
        workoutDetails_txtNoExercises = findViewById(R.id.workoutDetails_txtNoExercises);

        workoutDetails_imgBack = findViewById(R.id.workoutDetails_imgBack);

        workoutDetails_recViewExercises = findViewById(R.id.workoutDetails_recViewExercises);

        workoutDetails_rootContainer.setVisibility(View.INVISIBLE);

        long exerciseId = getIntent().getLongExtra("workoutId", -1);

        if (exerciseId < 0) {
            finish();
        } else {
            fetchWorkout(exerciseId);
        }

        workoutDetails_imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchWorkout(long id) {
        queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url + "/" + id, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        workout = Workout.fromJson(getApplicationContext(), response);

                        if (workout == null) {
                            finish();
                            return;
                        }

                        populateUI();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if (error == null) {
                            Toast.makeText(getApplicationContext(), "An unknown error has occured", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        error.printStackTrace();

                        if (error.networkResponse == null) {
                            Toast.makeText(getApplicationContext(), "A network error has occured", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unknown application error has occured", Toast.LENGTH_LONG).show();
                        }

                        finish();
                    }
                }) {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + LocalStorage.getJwt(getApplicationContext()));
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void populateUI() {
        workoutDetails_name.setText(workout.getName());

        if (workout.getComment() != null && workout.getComment().length() > 0) {
            workoutDetails_comments.setText(workout.getComment());
        } else {
            workoutDetails_commentsContainer.setVisibility(View.GONE);
        }

        workoutDetails_date.setText(workout.getStartDate());

        if (workout.getStartTime() == null && workout.getEndTime() == null) {
            workoutDetails_time.setVisibility(View.GONE);
        } else if (workout.getStartTime() != null && workout.getEndTime() == null) {
            workoutDetails_time.setText("Started at " + workout.getStartTime());
        } else if (workout.getStartTime() == null && workout.getEndTime() != null) {
            workoutDetails_time.setText("Ended at " + workout.getEndTime());
        } else {
            workoutDetails_time.setText(workout.getStartTime() + " - " + workout.getEndTime());
        }

        if (workout.getExercisesPerformed().size() > 0) {
            workoutDetails_recViewExercises.setVisibility(View.VISIBLE);
            workoutDetails_txtNoExercises.setVisibility(View.GONE);
            exercisesRecycler(workout.getExercisesPerformed());
        } else {
            workoutDetails_recViewExercises.setVisibility(View.GONE);
            workoutDetails_txtNoExercises.setVisibility(View.VISIBLE);
        }

        workoutDetails_rootContainer.setVisibility(View.VISIBLE);
    }

    private void exercisesRecycler(List<ExercisePerformed> exercisesPerformed) {
        workoutDetails_recViewExercises.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        exercisesAdapter = new ExercisesAdapter(exercisesPerformed, new RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
        }, false);

        workoutDetails_recViewExercises.setAdapter(exercisesAdapter);
    }
}