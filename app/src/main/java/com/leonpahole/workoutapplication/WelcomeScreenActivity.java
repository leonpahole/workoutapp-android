package com.leonpahole.workoutapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class WelcomeScreenActivity extends AppCompatActivity {

    private WelcomeScreenAdapter welcomeScreenAdapter;
    private LinearLayout welcome_indicatorLayout;
    private ViewPager2 welcomeScreenPager;

    Button welcome_btnNext, welcome_btnNextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        welcome_indicatorLayout = findViewById(R.id.welcome_indicatorLayout);

        setupWelcomeItems();

        welcomeScreenPager = findViewById(R.id.welcome_viewPager);
        welcomeScreenPager.setAdapter(welcomeScreenAdapter);

        welcome_btnNext = findViewById(R.id.welcome_btnNext);
        welcome_btnNextActivity = findViewById(R.id.welcome_btnNextActivity);

        setupIndicators();
        setActiveIndicator(0);

        welcomeScreenPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setActiveIndicator(position);

                if (position == welcomeScreenAdapter.getItemCount() - 1) {
                    welcome_btnNext.setVisibility(View.INVISIBLE);
                    welcome_btnNextActivity.setVisibility(View.VISIBLE);
                } else {
                    welcome_btnNext.setVisibility(View.VISIBLE);
                    welcome_btnNextActivity.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setupWelcomeItems() {
        List<WelcomeScreenItem> welcomeScreenItems = new ArrayList<>();

        WelcomeScreenItem trackProgress = new WelcomeScreenItem();
        trackProgress.setImage(R.drawable.im_track);
        trackProgress.setTitle("Track your progress");
        trackProgress.setSubtitle("Track your exercise progress by simply logging the workouts and using templates");

        WelcomeScreenItem makeProgress = new WelcomeScreenItem();
        makeProgress.setImage(R.drawable.im_progress);
        makeProgress.setTitle("Make progress");
        makeProgress.setSubtitle("Make and log your progress using the intiutive interface");

        WelcomeScreenItem reachGoals = new WelcomeScreenItem();
        reachGoals.setImage(R.drawable.im_success);
        reachGoals.setTitle("Reach your goals");
        reachGoals.setSubtitle(getString(R.string.app_name) + " will help you reach your exercise goals!");

        welcomeScreenItems.add(trackProgress);
        welcomeScreenItems.add(makeProgress);
        welcomeScreenItems.add(reachGoals);

        welcomeScreenAdapter = new WelcomeScreenAdapter(welcomeScreenItems);

    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[welcomeScreenAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.welcome_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            welcome_indicatorLayout.addView(indicators[i]);
        }
    }

    private void setActiveIndicator(int position) {
        int childCount = welcome_indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) welcome_indicatorLayout.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.welcome_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.welcome_indicator_inactive));
            }
        }
    }

    public void nextSlide(View v) {
        int nextItemIndex = welcomeScreenPager.getCurrentItem() + 1;

        if (nextItemIndex < welcomeScreenAdapter.getItemCount()) {
            welcomeScreenPager.setCurrentItem(nextItemIndex);
        }
    }

    public void goToNextActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), ChooseLoginMethodActivity.class);
        startActivity(intent);
        finish();
    }
}