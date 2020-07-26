package com.leonpahole.workoutapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leonpahole.workoutapplication.utils.adapters.ExercisesAdapter;
import com.leonpahole.workoutapplication.utils.exercises.Workout;

import java.util.List;

public class WorkoutListAdapter extends RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder> {

    private List<Workout> workouts;
    RecyclerViewOnClickListener listener;

    public WorkoutListAdapter(List<Workout> workouts, RecyclerViewOnClickListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.workout_list_item, parent, false
                ),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutListViewHolder holder, int position) {
        holder.setWorkoutData(workouts.get(position));
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class WorkoutListViewHolder extends RecyclerView.ViewHolder {

        private TextView workoutListItem_name, workoutListItem_date,
                workoutListItem_startTime, workoutListItem_endTime;

        private View workoutListItem_rootContainer, workoutListItem_timeContainer;

        public WorkoutListViewHolder(@NonNull View itemView, final RecyclerViewOnClickListener listener) {
            super(itemView);
            workoutListItem_name = itemView.findViewById(R.id.workoutListItem_name);
            workoutListItem_date = itemView.findViewById(R.id.workoutListItem_date);
            workoutListItem_startTime = itemView.findViewById(R.id.workoutListItem_startTime);
            workoutListItem_endTime = itemView.findViewById(R.id.workoutListItem_endTime);
            workoutListItem_rootContainer = itemView.findViewById(R.id.workoutListItem_rootContainer);
            workoutListItem_timeContainer = itemView.findViewById(R.id.workoutListItem_timeContainer);

            workoutListItem_rootContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, getAdapterPosition());
                }
            });
        }

        void setWorkoutData(Workout workout) {
            workoutListItem_name.setText(workout.getName());
            workoutListItem_date.setText(workout.getStartDate());

            if (workout.getStartTime() == null || workout.getEndTime() == null) {
                workoutListItem_timeContainer.setVisibility(View.INVISIBLE);
            } else {
                workoutListItem_timeContainer.setVisibility(View.VISIBLE);
                workoutListItem_startTime.setText(workout.getStartTime());
                workoutListItem_endTime.setText(workout.getEndTime());
            }
        }
    }
}
