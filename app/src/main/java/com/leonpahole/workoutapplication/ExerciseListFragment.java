package com.leonpahole.workoutapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.adapters.ExerciseListAdapter;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addExerciseButton();
    }

    private void addExerciseButton() {
        exerciseList_btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AddExerciseDialog addExerciseDialog = new AddExerciseDialog();
                addExerciseDialog.show(getFragmentManager(), "Add exercise dialog");
                addExerciseDialog.setListener(new AddExerciseDialog.AddExerciseDialogListener() {
                    @Override
                    public void applyExercise(String name, String category) {
                        addOrEditExercise(name, category, null);
                    }
                });
            }
        });
    }

    private void exercisesRecycler() {
        exerciseList_recExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        exercisesAdapter = new ExerciseListAdapter(exercises, new RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, final int position) {

                final Exercise exerciseToUpdate = exercises.get(position);

                if (exerciseToUpdate.getAuthorId() == null) {
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("exerciseName", exerciseToUpdate.getName());

                final AddExerciseDialog addExerciseDialog = new AddExerciseDialog();
                addExerciseDialog.show(getFragmentManager(), "Edit exercise dialog");
                addExerciseDialog.setArguments(bundle);
                addExerciseDialog.setListener(new AddExerciseDialog.AddExerciseDialogListener() {
                    @Override
                    public void applyExercise(String name, String category) {
                        addOrEditExercise(name, category, position);
                    }
                });
            }
        }, new RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, final int position) {

                final Exercise exerciseToDelete = exercises.get(position);

                if (exerciseToDelete.getAuthorId() == null) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete exercise?");
                builder.setMessage("Do you really want to delete exercise " + exerciseToDelete.getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.DELETE, url + "/" + exerciseToDelete.getId(), new JSONObject(), new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        exercises.remove(exerciseToDelete);
                                        exercisesAdapter.notifyItemRemoved(position);

                                        LocalStorage.saveExercises(getContext(), exercises);
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
                });

                builder.setNegativeButton("No", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        exerciseList_recExercises.setAdapter(exercisesAdapter);
    }

    public void addOrEditExercise(String name, String category, @Nullable final Integer positionOfEdit) {

        final boolean isEdit = positionOfEdit != null;
        int method = isEdit ? Request.Method.PATCH : Request.Method.POST;

        String requestUrl = url + "/";

        if (isEdit) {
            Exercise exerciseToUpdate = exercises.get(positionOfEdit);

            if (exerciseToUpdate.getName().equals(name) &&
                    exerciseToUpdate.getCategory().getCode().equals(category)) {
                return;
            }

            requestUrl += exerciseToUpdate.getId();
        }

        JSONObject addOrEditRequest;

        try {
            addOrEditRequest = new JSONObject()
                    .put("name", name)
                    .put("category", category);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, requestUrl, addOrEditRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.has("name") && !response.isNull("name")) {

                                Exercise exercise = GsonUtil.getGsonParser().fromJson(response.toString(), Exercise.class);

                                if (isEdit) {
                                    exercises.set(positionOfEdit, exercise);
                                    exercisesAdapter.notifyItemChanged(positionOfEdit);
                                } else {
                                    exercises.add(0, exercise);
                                    exercisesAdapter.notifyItemInserted(0);
                                }


                                LocalStorage.saveExercises(getContext(), exercises);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getContext(), "A data error has occured", Toast.LENGTH_SHORT).show();
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