package com.leonpahole.workoutapplication.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leonpahole.workoutapplication.RecyclerViewOnClickListener;
import com.leonpahole.workoutapplication.R;
import com.leonpahole.workoutapplication.utils.exercises.ExerciseCategory;
import com.leonpahole.workoutapplication.utils.exercises.ExercisePerformed;
import com.leonpahole.workoutapplication.utils.exercises.SetPerformed;

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder> {

    List<ExercisePerformed> exercisesPerformed;
    RecyclerViewOnClickListener listener;

    public ExercisesAdapter(List<ExercisePerformed> exercisesPerformed, RecyclerViewOnClickListener listener) {
        this.exercisesPerformed = exercisesPerformed;
        this.listener = listener;
    }

    public class ExercisesViewHolder extends RecyclerView.ViewHolder {

        TextView exercisesListItem_txtTitle;
        LinearLayout exercisesListItem_layoutSets;

        ImageView exercisesListItem_imgDelete, exercisesListItem_imgEdit;

        public ExercisesViewHolder(@NonNull View itemView, final RecyclerViewOnClickListener listener) {
            super(itemView);

            exercisesListItem_txtTitle = itemView.findViewById(R.id.exercisesListItem_txtTitle);
            exercisesListItem_layoutSets = itemView.findViewById(R.id.exercisesListItem_layoutSets);
            exercisesListItem_imgDelete = itemView.findViewById(R.id.exercisesListItem_imgDelete);
            exercisesListItem_imgEdit = itemView.findViewById(R.id.exercisesListItem_imgEdit);

            exercisesListItem_imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(getAdapterPosition());
                }
            });

            exercisesListItem_imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, getAdapterPosition());
                }
            });
        }
    }

    public void removeAt(int position) {
        exercisesPerformed.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, exercisesPerformed.size());
    }

    @NonNull
    @Override
    public ExercisesAdapter.ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_list_item, parent, false);
        ExercisesAdapter.ExercisesViewHolder exercisesViewHolder = new ExercisesAdapter.ExercisesViewHolder(view, listener);
        return exercisesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesAdapter.ExercisesViewHolder holder, final int position) {
        ExercisePerformed exercisePerformed = exercisesPerformed.get(position);

        holder.exercisesListItem_txtTitle.setText(exercisePerformed.getExercise().getName());
        holder.exercisesListItem_layoutSets.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(holder.exercisesListItem_txtTitle.getContext());
        int setNumber = 1;
        for (SetPerformed set : exercisePerformed.getSets()) {
            View child = inflater.inflate(R.layout.set_line, null);
            ((TextView) child.findViewById(R.id.setLine_txtSetInformation)).setText(set.toString());

            String setNumberString = "Set " + setNumber + ":";
            if (exercisePerformed.getExercise().getCategory() == ExerciseCategory.CARDIO) {
                setNumberString = "";
            }

            ((TextView) child.findViewById(R.id.setLine_txtSetNumber)).setText(setNumberString);

            holder.exercisesListItem_layoutSets.addView(child);
            setNumber++;
        }
    }

    @Override
    public int getItemCount() {
        return exercisesPerformed.size();
    }
}
