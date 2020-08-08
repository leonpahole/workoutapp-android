package com.leonpahole.workoutapplication.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leonpahole.workoutapplication.R;
import com.leonpahole.workoutapplication.RecyclerViewOnClickListener;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;

import java.util.List;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder> {

    private List<Exercise> exercises;
    RecyclerViewOnClickListener deleteListener, editListener;

    public ExerciseListAdapter(List<Exercise> exercises, RecyclerViewOnClickListener editListener,
                               RecyclerViewOnClickListener deleteListener) {
        this.exercises = exercises;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ExerciseListAdapter.ExerciseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExerciseListAdapter.ExerciseListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.exercise_list_item, parent, false
                ),
                editListener, deleteListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListAdapter.ExerciseListViewHolder holder, int position) {
        holder.setWorkoutData(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    class ExerciseListViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseListItem_name, exerciseListItem_type, exerciseListItem_typeLabel;
        private View exerciseListItem_layoutWrapper;

        private ImageView exerciseListItem_btnEdit, exerciseListItem_btnDelete;

        private RecyclerViewOnClickListener editListener, deleteListener;

        public ExerciseListViewHolder(@NonNull View itemView,
                                      final RecyclerViewOnClickListener editListener,
                                      final RecyclerViewOnClickListener deleteListener) {
            super(itemView);
            exerciseListItem_name = itemView.findViewById(R.id.exerciseListItem_name);
            exerciseListItem_type = itemView.findViewById(R.id.exerciseListItem_type);
            exerciseListItem_typeLabel = itemView.findViewById(R.id.exerciseListItem_typeLabel);
            exerciseListItem_layoutWrapper = itemView.findViewById(R.id.exerciseListItem_layoutWrapper);

            exerciseListItem_btnEdit = itemView.findViewById(R.id.exerciseListItem_btnEdit);
            exerciseListItem_btnDelete = itemView.findViewById(R.id.exerciseListItem_btnDelete);

            this.editListener = editListener;
            this.deleteListener = deleteListener;
        }

        void setWorkoutData(Exercise exercise) {
            exerciseListItem_name.setText(exercise.getName());
            exerciseListItem_type.setText(exercise.getCategory() != null ? exercise.getCategory().getName() : "");

            /*
            if (getAdapterPosition() % 2 != 0) {
                exerciseListItem_layoutWrapper.setBackgroundColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.white));
                exerciseListItem_name.setTextColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.black));
                exerciseListItem_typeLabel.setTextColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.black));
                exerciseListItem_type.setTextColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.black));
            }
             */

            if (exercise.getAuthorId() == null) {
                exerciseListItem_btnEdit.setVisibility(View.GONE);
                exerciseListItem_btnDelete.setVisibility(View.GONE);
            } else {
                exerciseListItem_btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editListener.onClick(v, getAdapterPosition());
                    }
                });

                exerciseListItem_btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteListener.onClick(v, getAdapterPosition());
                    }
                });
            }
        }
    }

}
