package com.leonpahole.workoutapplication.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.leonpahole.workoutapplication.R;
import com.leonpahole.workoutapplication.RecyclerViewOnClickListener;
import com.leonpahole.workoutapplication.WorkoutListAdapter;
import com.leonpahole.workoutapplication.utils.exercises.Exercise;
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import java.util.List;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder> {

    private List<Exercise> exercises;
    RecyclerViewOnClickListener listener;

    public ExerciseListAdapter(List<Exercise> exercises, RecyclerViewOnClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseListAdapter.ExerciseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExerciseListAdapter.ExerciseListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.exercise_list_item, parent, false
                ),
                listener
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

        public ExerciseListViewHolder(@NonNull View itemView, final RecyclerViewOnClickListener listener) {
            super(itemView);
            exerciseListItem_name = itemView.findViewById(R.id.exerciseListItem_name);
            exerciseListItem_type = itemView.findViewById(R.id.exerciseListItem_type);
            exerciseListItem_typeLabel = itemView.findViewById(R.id.exerciseListItem_typeLabel);
            exerciseListItem_layoutWrapper = itemView.findViewById(R.id.exerciseListItem_layoutWrapper);
        }

        void setWorkoutData(Exercise exercise) {
            exerciseListItem_name.setText(exercise.getName());
            exerciseListItem_type.setText(exercise.getCategory() != null ? exercise.getCategory().getName() : "");

            if (getAdapterPosition() % 2 != 0) {
                exerciseListItem_layoutWrapper.setBackgroundColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.white));
                exerciseListItem_name.setTextColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.black));
                exerciseListItem_typeLabel.setTextColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.black));
                exerciseListItem_type.setTextColor(ContextCompat.getColor(exerciseListItem_layoutWrapper.getContext(), R.color.black));
            }
        }
    }

}
