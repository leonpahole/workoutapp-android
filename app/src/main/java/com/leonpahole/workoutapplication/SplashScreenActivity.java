package com.leonpahole.workoutapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.leonpahole.workoutapplication.utils.LocalStorage;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;
    private static final String SP_WELCOME_SCREEN = "welcomeScreen";
    private static final String SP_WELCOME_SCREEN_ALREADY_SEEN = "welcomeScreenAlreadySeen";
    private static final boolean WRITE_TO_PREFERENCES = false;
    private static final boolean SKIP_INSTRUCTIONS = true;

    TextView splash_txtTitle, splash_txtSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        splash_txtTitle = findViewById(R.id.splash_txtTitle);
        splash_txtSubtitle = findViewById(R.id.splash_txtSubtitle);

        splash_txtTitle.setAnimation(fadeIn);
        splash_txtSubtitle.setAnimation(fadeIn);

        // after some time go to next screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // only show welcome screen once
                SharedPreferences welcomeScreenPreferences = getSharedPreferences(SP_WELCOME_SCREEN, MODE_PRIVATE);
                boolean welcomeScreenAlreadySeen = SKIP_INSTRUCTIONS || welcomeScreenPreferences.getBoolean(SP_WELCOME_SCREEN_ALREADY_SEEN, false);

                Intent intent;

                if (!welcomeScreenAlreadySeen) {

                    // write to preferences and go to welcome screen
                    if (WRITE_TO_PREFERENCES) {
                        SharedPreferences.Editor editor = welcomeScreenPreferences.edit();
                        editor.putBoolean(SP_WELCOME_SCREEN_ALREADY_SEEN, true);
                        editor.apply();
                    }

                    intent = new Intent(getApplicationContext(), WelcomeScreenActivity.class);

                }
                // todo: check logged in
                else {
                    // go to login screen
                    boolean isLoggedIn = LocalStorage.isLoggedIn(getApplicationContext());
                    if (isLoggedIn) {
                        intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    } else {
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                    }
                }

                startActivity(intent);
                               finish();
            }
        }, SPLASH_DURATION);
    }
}