package com.leonpahole.workoutapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;

import java.util.Arrays;
import java.util.List;

public class AddExerciseDialog extends DialogFragment {

    private TextInputLayout addExerciseDialog_iptName;
    private AddExerciseDialog.AddExerciseDialogListener listener;
    private Spinner addExerciseDialog_iptCategory;

    private List<ExerciseCategory> categories;

    public void setListener(AddExerciseDialog.AddExerciseDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_exercise_dialog, null);

        addExerciseDialog_iptName = view.findViewById(R.id.addExerciseDialog_iptName);
        addExerciseDialog_iptCategory = view.findViewById(R.id.addExerciseDialog_iptCategory);

        categorySpinner();

        Bundle arguments = getArguments();
        boolean isEdit = false;

        if (arguments != null) {

            String exerciseNameToEdit = arguments.getString("exerciseName", null);

            if (exerciseNameToEdit != null) {
                addExerciseDialog_iptName.getEditText().setText(exerciseNameToEdit);
                isEdit = true;
            }
        }

        builder.setView(view)
                .setTitle(isEdit ? "Edit exercise" : "Add an exercise")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(isEdit ? "Edit" : "Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!validateInputs()) {
                            return;
                        }

                        listener.applyExercise(addExerciseDialog_iptName.getEditText().getText().toString().trim(),
                                ((ExerciseCategory) addExerciseDialog_iptCategory.getSelectedItem()).getCode());

                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    }

    private void categorySpinner() {

        categories = Arrays.asList(ExerciseCategory.values());

        ArrayAdapter<ExerciseCategory> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        addExerciseDialog_iptCategory.setAdapter(adapter);
    }

    public boolean validateInputs() {

        boolean isValid = true;

        String exerciseName = addExerciseDialog_iptName.getEditText().getText().toString().trim();
        if (exerciseName.isEmpty()) {
            addExerciseDialog_iptName.setError("Name is required");
            isValid = false;
        }

        if (addExerciseDialog_iptCategory.getSelectedItem() == null) {
            ((TextView) addExerciseDialog_iptCategory.getSelectedView()).setError("Select category");
            isValid = false;
        }

        return isValid;
    }

    public interface AddExerciseDialogListener {
        void applyExercise(String name, String category);
    }
}
