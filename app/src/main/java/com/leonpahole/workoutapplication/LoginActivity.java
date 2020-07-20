package com.leonpahole.workoutapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    TextView login_txtLogo, login_txtLoginTitle, login_txtLoginSubtitle;
    Button login_btnLogin, login_btnNoAccountYet, login_btnSwitchMethod;
    TextInputLayout login_iptEmail, login_iptPassword;

    RequestQueue queue;
    String url = "http://192.168.1.67:8080/api/user/login";

    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        queue = Volley.newRequestQueue(this);

        login_txtLogo = findViewById(R.id.login_txtLogo);
        login_txtLoginTitle = findViewById(R.id.login_txtLoginTitle);
        login_txtLoginSubtitle = findViewById(R.id.login_txtLoginSubtitle);

        login_iptEmail = findViewById(R.id.login_iptEmail);
        login_iptPassword = findViewById(R.id.login_iptPassword);

        login_btnLogin = findViewById(R.id.login_btnLogin);
        login_btnNoAccountYet = findViewById(R.id.login_btnNoAccountYet);
        login_btnSwitchMethod = findViewById(R.id.login_btnSwitchMethod);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            login_txtLogo.setTransitionName("transition_signupLogin_txtLogo");
            login_txtLoginTitle.setTransitionName("transition_signupLogin_txtTitle");
            login_txtLoginSubtitle.setTransitionName("transition_signupLogin_txtSubtitle");

            login_iptEmail.setTransitionName("transition_signupLogin_iptEmail");
            login_iptPassword.setTransitionName("transition_signupLogin_iptPassword");

            login_btnLogin.setTransitionName("transition_signupLogin_btnPrimary");
            login_btnNoAccountYet.setTransitionName("transition_signupLogin_btnSwitchPage");
            login_btnSwitchMethod.setTransitionName("transition_signupLogin_btnSwitchMethod");
        }

        if (DEBUG) {
            login_iptEmail.getEditText().setText("leon2@gmail.com");
            login_iptPassword.getEditText().setText("test");
        }
    }

    public void onNoAccountYet(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create((View) login_txtLogo, "transition_signupLogin_txtLogo"),
                    Pair.create((View) login_txtLoginTitle, "transition_signupLogin_txtTitle"),
                    Pair.create((View) login_txtLoginSubtitle, "transition_signupLogin_txtSubtitle"),
                    Pair.create((View) login_iptEmail, "transition_signupLogin_iptEmail"),
                    Pair.create((View) login_iptPassword, "transition_signupLogin_iptPassword"),
                    Pair.create((View) login_btnLogin, "transition_signupLogin_btnPrimary"),
                    Pair.create((View) login_btnNoAccountYet, "transition_signupLogin_btnSwitchPage"),
                    Pair.create((View) login_btnSwitchMethod, "transition_signupLogin_btnSwitchMethod")
            );

            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void onSwitchLoginMethod(View v) {
        Intent intent = new Intent(getApplicationContext(), ChooseLoginMethodActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }

        finish();
    }

    public void onLogin(View view) throws JSONException {
        if (!validateEmail() | !validatePassword()) {
            return;
        }

        final String password = login_iptPassword.getEditText().getText().toString();
        final String email = login_iptEmail.getEditText().getText().toString();

        final JSONObject loginRequest = new JSONObject()
                .put("email", email)
                .put("password", password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, loginRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.isNull("token") || response.isNull("user")) {
                            Toast.makeText(getApplicationContext(), "Missing data error has occured", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                String name = response.getJSONObject("user").getString("name");
                                Toast.makeText(getApplicationContext(), "Welcome, " + name + "!", Toast.LENGTH_LONG).show();

                                String token = response.getString("token");
                                LocalStorage.saveJwt(getApplicationContext(), token);

                                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Data error has occured", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if (error == null) {
                            Toast.makeText(getApplicationContext(), "An unknown error has occured", Toast.LENGTH_LONG).show();
                            return;
                        }

                        error.printStackTrace();

                        if (error.networkResponse == null) {
                            Toast.makeText(getApplicationContext(), "A network error has occured", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect email or password combination!", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        queue.add(jsonObjectRequest);
    }

    private boolean validateEmail() {
        String val = login_iptEmail.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            login_iptEmail.setError("Please enter your email");
            return false;
        }

        login_iptEmail.setError(null);
        login_iptEmail.setErrorEnabled(false);

        return true;
    }

    private boolean validatePassword() {
        String val = login_iptPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            login_iptPassword.setError("Please enter your password");
            return false;
        }

        login_iptPassword.setError(null);
        login_iptPassword.setErrorEnabled(false);

        return true;
    }
}