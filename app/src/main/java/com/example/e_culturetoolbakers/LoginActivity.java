package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputUserEmail, inputPassword;
    private Button btnLogin;
    private TextView register, guestLogin;
    private ProgressBar progressBar;
    private Locale locale;
    private Configuration config;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Locale current = getResources().getConfiguration().locale;
        SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        if (preferences.getString("My_Lang", "") == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("My_Lang", String.valueOf(current));
            editor.apply();
        } else {
            locale = new Locale(preferences.getString("My_Lang", ""));
            Locale.setDefault(locale);
            config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        inputUserEmail = findViewById(R.id.loginEmail);
        inputPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        guestLogin = findViewById(R.id.guestLogin);
        register = findViewById(R.id.buttonRegisterFromLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(this);
        guestLogin.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                loginUser();
                break;
            case R.id.buttonRegisterFromLogin:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.guestLogin:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    private void loginUser() {
        String email = inputUserEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (checkInfo(email, password)) {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);

                        //redirect to homepage layout
                        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Accesso fallito. Controlla le tue credenziali", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private boolean checkInfo(String email, String password) {
        if (email.isEmpty()) {
            inputUserEmail.setError("Inserisci l'email");
            inputUserEmail.requestFocus();
            return false;

        } else if (password.isEmpty()) {
            inputPassword.setError("Inserisci la password");
            inputPassword.requestFocus();
            return false;
        }
        return true;
    }
}