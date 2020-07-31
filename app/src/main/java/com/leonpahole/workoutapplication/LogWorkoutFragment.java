package com.leonpahole.workoutapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.adapters.ExercisesAdapter;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogWorkoutFragment extends Fragment implements NewExerciseDialog.NewExerciseDialogListener {

    TextInputLayout logWorkout_iptStartTime, logWorkout_iptEndTime, logWorkout_iptStartDate,
            logWorkout_iptName, logWorkout_iptComment;
    RecyclerView logWorkout_recExercises;
    RecyclerView.Adapter exercisesAdapter;

    Workout workout;

    Button logWorkout_btnAddExercise, logWorkout_btnSaveWorkout;

    String url = "http://192.168.1.68:8080/api/workout";
    RequestQueue queue;

    int currentEditingPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("workout")) {
            workout = GsonUtil.getGsonParser().fromJson(getArguments().getString("workout"), Workout.class);
        } else {
            workout = new Workout();
            workout.setExercisesPerformed(new ArrayList<ExercisePerformed>());
            workout.setStartDate(new SimpleDateFormat("dd. MM. yyyy").format(new Date()));
            workout.setEndTime(new SimpleDateFormat("HH:mm").format(new Date()));
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queue = Volley.newRequestQueue(getContext());

        logWorkout_iptStartDate = view.findViewById(R.id.logWorkout_iptStartDate);
        logWorkout_iptEndTime = view.findViewById(R.id.logWorkout_iptEndTime);
        logWorkout_iptStartTime = view.findViewById(R.id.logWorkout_iptStartTime);
        logWorkout_iptName = view.findViewById(R.id.logWorkout_iptName);
        logWorkout_iptComment = view.findViewById(R.id.logWorkout_iptComment);

        logWorkout_recExercises = view.findViewById(R.id.logWorkout_recExercises);
        logWorkout_btnAddExercise = view.findViewById(R.id.logWorkout_btnAddExercise);
        logWorkout_btnSaveWorkout = view.findViewById(R.id.logWorkout_btnSaveWorkout);

        dateTimePickers();
        exercisesRecycler();
        addExerciseButton();
        saveWorkoutButton();

        if (workout.getExercisesPerformed().size() > 0) {
            exercisesAdapter.notifyItemRangeInserted(0, workout.getExercisesPerformed().size());
        }
    }

    private void exercisesRecycler() {
        // logWorkout_recExercises.setHasFixedSize(true);
        logWorkout_recExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // logWorkout_recExercises.setNestedScrollingEnabled(false);

        exercisesAdapter = new ExercisesAdapter(workout.getExercisesPerformed(), new RecyclerViewOnClickListener() {
            @Override
            public void onClick(View view, int position) {
                onExerciseEdit(position);
            }
        });

        logWorkout_recExercises.setAdapter(exercisesAdapter);
    }

    private void addExerciseButton() {
        logWorkout_btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearFocus();
                clearKeyboard();

                NewExerciseDialog newExerciseDialog = new NewExerciseDialog();
                newExerciseDialog.show(getFragmentManager(), "New exercise dialog");
                newExerciseDialog.setListener(LogWorkoutFragment.this);
            }
        });
    }

    private void saveWorkoutButton() {
        logWorkout_btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() | !validateDate()) {
                    return;
                }

                if (workout.getExercisesPerformed().size() == 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Save without exercises?")
                            .setMessage("Do you want to save the workout without exercises?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    sendSaveWorkout();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    sendSaveWorkout();
                }
            }
        });
    }

    private void sendSaveWorkout() {

        String name = logWorkout_iptName.getEditText().getText().toString().trim();
        String comment = logWorkout_iptComment.getEditText().getText().toString().trim();
        String startDate = logWorkout_iptStartDate.getEditText().getText().toString().trim();
        String startTime = logWorkout_iptStartTime.getEditText().getText().toString().trim();
        String endTime = logWorkout_iptEndTime.getEditText().getText().toString().trim();

        try {
            final JSONObject loginRequest = new JSONObject()
                    .put("name", name)
                    .put("comment", comment)
                    .put("startDate", startDate)
                    .put("startTime", startTime)
                    .put("endTime", endTime)
                    .put("exercisesPerformed", ExercisePerformed.toJson(workout.getExercisesPerformed()));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, loginRequest, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            if (response.has("id") && !response.isNull("id")) {

                                Toast.makeText(getContext(), "Workout added!", Toast.LENGTH_LONG).show();
                                clearInputs();

                                Intent intent = new Intent(getContext(), WorkoutDetailsActivity.class);

                                try {
                                    intent.putExtra("workoutId", response.getLong("id"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getContext(), "A data error has occured", Toast.LENGTH_LONG).show();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validateName() {
        String name = logWorkout_iptName.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            logWorkout_iptName.setError("Enter workout name");
            return false;
        }

        logWorkout_iptName.setError(null);
        logWorkout_iptName.setErrorEnabled(false);

        return true;
    }

    private boolean validateDate() {
        String date = logWorkout_iptStartDate.getEditText().getText().toString().trim();
        if (date.isEmpty()) {
            logWorkout_iptStartDate.setError("Enter date of workout");
            return false;
        }

        logWorkout_iptStartDate.setError(null);
        logWorkout_iptStartDate.setErrorEnabled(false);

        return true;
    }

    private void onExerciseEdit(int position) {
        ExercisePerformed exercisePerformed = workout.getExercisesPerformed().get(position);

        currentEditingPosition = position;

        Bundle bundle = new Bundle();
        bundle.putString("exercisePerformed", GsonUtil.getGsonParser().toJson(exercisePerformed));

        NewExerciseDialog newExerciseDialog = new NewExerciseDialog();
        newExerciseDialog.show(getFragmentManager(), "New exercise dialog");
        newExerciseDialog.setArguments(bundle);
        newExerciseDialog.setListener(LogWorkoutFragment.this);
    }

    private void dateTimePickers() {

        logWorkout_iptStartDate.getEditText().setText(workout.getStartDate());
        logWorkout_iptStartTime.getEditText().setText(workout.getStartTime());
        logWorkout_iptEndTime.getEditText().setText(workout.getEndTime());

        logWorkout_iptStartDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus();
                clearKeyboard();

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                logWorkout_iptStartDate.getEditText().setText(
                                        String.format("%02d", dayOfMonth) + ". " + String.format("%02d", monthOfYear + 1) + ". " + year);
                            }

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        logWorkout_iptStartTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus();
                clearKeyboard();

                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                logWorkout_iptStartTime.getEditText().setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        logWorkout_iptEndTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus();
                clearKeyboard();

                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                logWorkout_iptEndTime.getEditText().setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private void clearFocus() {
        if (getActivity().getCurrentFocus() != null) {
            getActivity().getCurrentFocus().clearFocus();
        }
    }

    @Override
    public void applyExercise(ExercisePerformed exercisePerformed) {
        if (currentEditingPosition >= 0) {
            // edit
            workout.getExercisesPerformed().set(currentEditingPosition, exercisePerformed);
            exercisesAdapter.notifyItemChanged(currentEditingPosition);
        } else {
            // add
            workout.getExercisesPerformed().add(exercisePerformed);
            exercisesAdapter.notifyItemInserted(workout.getExercisesPerformed().size() - 1);
        }

        currentEditingPosition = -1;
        clearKeyboard();
    }

    public void clearInputs() {
        logWorkout_iptName.getEditText().setText("");
        logWorkout_iptStartDate.getEditText().setText("");
        logWorkout_iptStartTime.getEditText().setText("");
        logWorkout_iptEndTime.getEditText().setText("");
        logWorkout_iptComment.getEditText().setText("");

        int size = workout.getExercisesPerformed().size();
        workout.getExercisesPerformed().clear();
        exercisesAdapter.notifyItemRangeRemoved(0, size);
    }

    private void clearKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }
}