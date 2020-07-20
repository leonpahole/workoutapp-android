package com.leonpahole.workoutapplication;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WelcomeScreenAdapter extends RecyclerView.Adapter<WelcomeScreenAdapter.WelcomeViewHolder> {

    private List<WelcomeScreenItem> welcomeScreenItems;

    public WelcomeScreenAdapter(List<WelcomeScreenItem> welcomeScreenItems) {
        this.welcomeScreenItems = welcomeScreenItems;
    }

    @NonNull
    @Override
    public WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WelcomeViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_welcome_screen, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeViewHolder holder, int position) {
        holder.setWelcomeData(welcomeScreenItems.get(position));
    }

    @Override
    public int getItemCount() {
        return welcomeScreenItems.size();
    }

    class WelcomeViewHolder extends RecyclerView.ViewHolder {

        private TextView title, subtitle;
        private ImageView image;

        public WelcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.welcome_txtTitle);
            subtitle = itemView.findViewById(R.id.welcome_txtSubtitle);
            image = itemView.findViewById(R.id.welcome_imgMain);
        }

        void setWelcomeData(WelcomeScreenItem welcomeScreenItem) {
            title.setText(welcomeScreenItem.getTitle());
            subtitle.setText(welcomeScreenItem.getSubtitle());
            image.setImageResource(welcomeScreenItem.getImage());
        }
    }
}
