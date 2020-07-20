package com.leonpahole.workoutapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.leonpahole.workoutapplication.utils.LocalStorage;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout dashboard_drawerLayout;
    NavigationView dashboard_navigationView;
    LinearLayout dashboard_contentLayout;
    ImageView dashboard_imgMenu;

    private static final float END_SCALE = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dashboard_contentLayout = findViewById(R.id.dashboard_contentLayout);

        dashboard_imgMenu = findViewById(R.id.dashboard_imgMenu);

        dashboard_drawerLayout = findViewById(R.id.dashboard_drawerLayout);
        dashboard_navigationView = findViewById(R.id.dashboard_navigationView);

        navigationDrawer();
        inflateLayoutFragment(DashboardHomeFragment.class);
    }

    private void navigationDrawer() {
        dashboard_navigationView.bringToFront();
        dashboard_navigationView.setNavigationItemSelectedListener(this);
        dashboard_navigationView.setCheckedItem(R.id.nav_btnHome);


        dashboard_imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dashboard_drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    dashboard_drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    dashboard_drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        dashboard_drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;

                dashboard_contentLayout.setScaleX(offsetScale);
                dashboard_contentLayout.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = dashboard_contentLayout.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                dashboard_contentLayout.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (dashboard_drawerLayout.isDrawerVisible(GravityCompat.START)) {
            dashboard_drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class fragmentClass;

        switch (item.getItemId()) {

            case R.id.nav_btnProfile:
                fragmentClass = ProfileFragment.class;
                break;

            case R.id.nav_btnLogWorkout:
                fragmentClass = LogWorkoutFragment.class;
                break;

            case R.id.nav_btnNewWorkout:
                fragmentClass = LiveWorkoutFragment.class;
                break;

            case R.id.nav_btnLogout:
                onLogout();
                return false;

            case R.id.nav_btnHome:
            default:
                fragmentClass = DashboardHomeFragment.class;
                break;
        }

        inflateLayoutFragment(fragmentClass);
        dashboard_drawerLayout.closeDrawers();
        return true;
    }

    public void inflateLayoutFragment(Class fragmentClass) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.dashboard_frameLayout, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectMenuItem(int id) {
        dashboard_navigationView.setCheckedItem(id);
    }

    private void onLogout() {
        LocalStorage.saveJwt(getApplicationContext(), null);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}