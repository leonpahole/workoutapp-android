package com.leonpahole.workoutapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
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

    List<ExercisePerformed> exercisesPerformed;

    Button logWorkout_btnAddExercise, logWorkout_btnSaveWorkout;

    String url = "http://192.168.1.68:8080/api/workout";
    RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercisesPerformed = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    }

    private void exercisesRecycler() {
        // logWorkout_recExercises.setHasFixedSize(true);
        logWorkout_recExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // logWorkout_recExercises.setNestedScrollingEnabled(false);

        exercisesAdapter = new ExercisesAdapter(exercisesPerformed, new ExerciseRecyclerViewOnClickListener() {
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

                if (exercisesPerformed.size() == 0) {
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
        String date = logWorkout_iptStartDate.getEditText().getText().toString().trim();
        String comment = logWorkout_iptName.getEditText().getText().toString().trim();
        String startTime = logWorkout_iptStartTime.getEditText().getText().toString().trim();
        String endTime = logWorkout_iptEndTime.getEditText().getText().toString().trim();

        DateFormat format = new SimpleDateFormat("dd. MM. yyyy hh:mm:ss");
        DateFormat formatOut = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        String startedAt = "";
        String endedAt = "";

        try {
            Date startedAtD = format.parse(date + " " + (!startTime.isEmpty() ? startTime + ":00" : "00:00:00"));
            Date endedAtD = format.parse(date + " " + (!endTime.isEmpty() ? endTime + ":00" : "00:00:00"));

            startedAt = formatOut.format(startedAtD);
            endedAt = formatOut.format(endedAtD);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            final JSONObject loginRequest = new JSONObject()
                    .put("name", name)
                    .put("comment", comment)
                    .put("startedAt", startedAt)
                    .put("endedAt", endedAt)
                    .put("exercisesPerformed", ExercisePerformed.toJson(exercisesPerformed));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, loginRequest, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getContext(), "Workout added!", Toast.LENGTH_LONG).show();
                            clearInputs();
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
        ExercisePerformed exercisePerformed = exercisesPerformed.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("exercisePerformed", GsonUtil.getGsonParser().toJson(exercisePerformed));
        bundle.putInt("editPosition", position);

        NewExerciseDialog newExerciseDialog = new NewExerciseDialog();
        newExerciseDialog.show(getFragmentManager(), "New exercise dialog");
        newExerciseDialog.setArguments(bundle);
        newExerciseDialog.setListener(LogWorkoutFragment.this);
    }

    private void dateTimePickers() {
        logWorkout_iptStartDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                logWorkout_iptStartDate.getEditText().setText(dayOfMonth + ". " + (monthOfYear + 1) + ". " + year);
                            }

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        logWorkout_iptStartTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                logWorkout_iptStartTime.getEditText().setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        logWorkout_iptEndTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                logWorkout_iptEndTime.getEditText().setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    @Override
    public void applyExercise(ExercisePerformed exercisePerformed, int editPosition) {
        if (editPosition >= 0) {
            // edit
            exercisesPerformed.set(editPosition, exercisePerformed);
            exercisesAdapter.notifyItemChanged(editPosition);
        } else {
            // add
            exercisesPerformed.add(exercisePerformed);
            exercisesAdapter.notifyItemInserted(exercisesPerformed.size() - 1);
        }
    }

    public void clearInputs() {
        logWorkout_iptName.getEditText().setText("");
        logWorkout_iptStartDate.getEditText().setText("");
        logWorkout_iptStartTime.getEditText().setText("");
        logWorkout_iptEndTime.getEditText().setText("");
        logWorkout_iptComment.getEditText().setText("");

        int size = exercisesPerformed.size();
        exercisesPerformed = new ArrayList<>();
        exercisesAdapter.notifyItemRangeRemoved(0, size);
    }
}