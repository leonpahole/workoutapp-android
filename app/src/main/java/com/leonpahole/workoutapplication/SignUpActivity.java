package com.leonpahole.workoutapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.leonpahole.workoutapplication.utils.GsonUtil;
import com.leonpahole.workoutapplication.utils.LocalStorage;
import com.leonpahole.workoutapplication.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout signup_iptName, signup_iptEmail, signup_iptPassword, signup_iptRepeatPassword;

    TextView signup_txtLogo, signup_txtSignupTitle, signup_txtSignupSubtitle;
    Button signup_btnSignup, signup_btnAlreadyHaveAccount, signup_btnSwitchMethod;

    RequestQueue queue;
    String url = "http://192.168.1.68:8080/api/user/register";

    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        queue = Volley.newRequestQueue(this);

        signup_txtLogo = findViewById(R.id.signup_txtLogo);
        signup_txtSignupTitle = findViewById(R.id.signup_txtSignupTitle);
        signup_txtSignupSubtitle = findViewById(R.id.signup_txtSignupSubtitle);

        signup_iptName = findViewById(R.id.signup_iptName);
        signup_iptEmail = findViewById(R.id.signup_iptEmail);
        signup_iptPassword = findViewById(R.id.signup_iptPassword);
        signup_iptRepeatPassword = findViewById(R.id.signup_iptRepeatPassword);

        signup_btnSignup = findViewById(R.id.signup_btnSignup);
        signup_btnAlreadyHaveAccount = findViewById(R.id.signup_btnAlreadyHaveAccount);
        signup_btnSwitchMethod = findViewById(R.id.signup_btnSwitchMethod);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            signup_txtLogo.setTransitionName("transition_signupLogin_txtLogo");
            signup_txtSignupTitle.setTransitionName("transition_signupLogin_txtTitle");
            signup_txtSignupSubtitle.setTransitionName("transition_signupLogin_txtSubtitle");

            signup_iptEmail.setTransitionName("transition_signupLogin_iptEmail");
            signup_iptPassword.setTransitionName("transition_signupLogin_iptPassword");

            signup_btnSignup.setTransitionName("transition_signupLogin_btnPrimary");
            signup_btnAlreadyHaveAccount.setTransitionName("transition_signupLogin_btnSwitchPage");
            signup_btnSwitchMethod.setTransitionName("transition_signupLogin_btnSwitchMethod");
        }

        if (DEBUG) {
            signup_iptName.getEditText().setText("Leon Pahole");
            signup_iptEmail.getEditText().setText("leon2@gmail.com");
            signup_iptPassword.getEditText().setText("test");
            signup_iptRepeatPassword.getEditText().setText("test");
        }
    }

    public void onSignUp(View view) throws JSONException {
        if (!validateName() | !validateEmail() | !validatePasswords()) {
            return;
        }

        final String name = signup_iptName.getEditText().getText().toString();
        final String password = signup_iptPassword.getEditText().getText().toString();
        final String email = signup_iptEmail.getEditText().getText().toString();

        final JSONObject registerRequest = new JSONObject()
                .put("email", email)
                .put("password", password)
                .put("name", name);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, registerRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.isNull("token") || response.isNull("user")) {
                            Toast.makeText(getApplicationContext(), "Missing data error has occured", Toast.LENGTH_LONG).show();
                        } else {

                            try {
                                String token = response.getString("token");
                                LocalStorage.saveJwt(getApplicationContext(), token, response.getJSONObject("user"));

                                Toast.makeText(getApplicationContext(), "Welcome, " + name + "!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
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
                        } else if (error.networkResponse.statusCode == 409) {
                            signup_iptEmail.setError("This email is already taken");
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), "An unknown application error has occured", Toast.LENGTH_LONG).show();
                        }

                        signup_iptEmail.setError(null);
                        signup_iptEmail.setErrorEnabled(false);
                    }
                });


        queue.add(jsonObjectRequest);
    }

    private boolean validateName() {
        String val = signup_iptName.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            signup_iptName.setError("Please enter your name");
            return false;
        }

        signup_iptName.setError(null);
        signup_iptName.setErrorEnabled(false);

        return true;
    }

    private boolean validateEmail() {
        String val = signup_iptEmail.getEditText().getText().toString().trim();
        boolean validEmail = (!TextUtils.isEmpty(val) && Patterns.EMAIL_ADDRESS.matcher(val).matches());

        if (val.isEmpty()) {
            signup_iptEmail.setError("Please enter your email");
            return false;
        } else if (!validEmail) {
            signup_iptEmail.setError("Email is not valid");
            return false;
        }

        signup_iptEmail.setError(null);
        signup_iptEmail.setErrorEnabled(false);

        return true;
    }

    private boolean validatePasswords() {
        String val = signup_iptPassword.getEditText().getText().toString();
        String valRepeat = signup_iptRepeatPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            signup_iptPassword.setError("Please enter your password");
            return false;
        } else if (val.length() < 4) {
            signup_iptPassword.setError("Please enter at least 4 characters");
            return false;
        }

        signup_iptPassword.setError(null);
        signup_iptPassword.setErrorEnabled(false);

        if (valRepeat.isEmpty()) {
            signup_iptRepeatPassword.setError("Please validate your password");
            return false;
        } else if (!val.equals(valRepeat)) {
            signup_iptRepeatPassword.setError("Passwords do not match");
            return false;
        }

        signup_iptRepeatPassword.setError(null);
        signup_iptRepeatPassword.setErrorEnabled(false);

        return true;
    }

    public void onAlreadyHaveAccount(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create((View) signup_txtLogo, "transition_signupLogin_txtLogo"),
                    Pair.create((View) signup_txtSignupTitle, "transition_signupLogin_txtTitle"),
                    Pair.create((View) signup_txtSignupSubtitle, "transition_signupLogin_txtSubtitle"),
                    Pair.create((View) signup_iptEmail, "transition_signupLogin_iptEmail"),
                    Pair.create((View) signup_iptPassword, "transition_signupLogin_iptPassword"),
                    Pair.create((View) signup_btnSignup, "transition_signupLogin_btnPrimary"),
                    Pair.create((View) signup_btnAlreadyHaveAccount, "transition_signupLogin_btnSwitchPage"),
                    Pair.create((View) signup_btnSwitchMethod, "transition_signupLogin_btnSwitchMethod")
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
}