package com.leonpahole.workoutapplication;

import androidx.appcompat.app.AppCompatActivity;

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
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WorkoutDetailsActivity extends AppCompatActivity {

    String url = "http://192.168.1.68:8080/api/workout";
    RequestQueue queue;
    Workout workout;

    View workoutDetails_rootContainer;
    TextView workoutDetails_name;
    ImageView workoutDetails_imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        workoutDetails_rootContainer = findViewById(R.id.workoutDetails_rootContainer);
        workoutDetails_name = findViewById(R.id.workoutDetails_name);
        workoutDetails_imgBack = findViewById(R.id.workoutDetails_imgBack);

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
        workoutDetails_rootContainer.setVisibility(View.VISIBLE);
    }
}