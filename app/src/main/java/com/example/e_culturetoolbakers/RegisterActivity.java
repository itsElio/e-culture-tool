package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView arrowBack;
    private EditText inputUsername, inputEmail, inputPassword, inputConfirmPassword;
    private RadioGroup radioTypesGroup;
    private Button btnRegister;
    private TextView alreadyHaveAccount;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        arrowBack = findViewById(R.id.arrowBack);

        inputUsername = findViewById(R.id.registrationUser);
        inputEmail = findViewById(R.id.registrationEmail);
        inputPassword = findViewById(R.id.registrationPassword);
        inputConfirmPassword = findViewById(R.id.registrationConfirmPassword);
        radioTypesGroup = findViewById(R.id.radioTypesGroup);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        btnRegister = findViewById(R.id.buttonRegistration);
        progressBar = findViewById(R.id.progressBar);

        arrowBack.setOnClickListener(this);
        alreadyHaveAccount.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegistration:
                registerUser();
                break;
            case R.id.alreadyHaveAccount:
            case R.id.arrowBack:
                finish();
                break;
        }
    }

    private void registerUser() {
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (checkInfo(username, email, password, inputConfirmPassword.getText().toString())) {
        //if (true) {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        String type = getType();
                        User user = new User(username, email, type);

                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.toast_registrationSucc), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                    //redirect to login layout
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.toast_registrationFail), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        //Toast.makeText(RegisterActivity.this, "Registrazione non avvenuta. Prova nuovamente", Toast.LENGTH_LONG.show());
                        Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private boolean checkInfo(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty()) {
            inputUsername.setError("Inserisci l'username");
            inputUsername.requestFocus();
            return false;

        } else if (email.isEmpty()) {
            inputEmail.setError("Inserisci l'email");
            inputEmail.requestFocus();
            return false;

        } else if (password.isEmpty()) {
            inputPassword.setError("Inserisci la password");
            inputPassword.requestFocus();
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Inserisci un'email valida");
            inputEmail.requestFocus();
            return false;

        } else if (password.length() < 6) {
            inputPassword.setError("La password deve contenere almeno 6 caratteri");
            inputPassword.requestFocus();
            return false;

        } else if (confirmPassword.isEmpty()) {
            inputPassword.setError("Inserisci la password di conferma");
            inputPassword.requestFocus();
            return false;

        } else if (!password.equals(confirmPassword)) {
            inputPassword.setError("Le password non corrispondono");
            inputPassword.requestFocus();
            return false;
        }

        return true;
    }

    private String getType() {
        int selectedRadioButtonId = radioTypesGroup.getCheckedRadioButtonId();
        String type = "";
        switch (selectedRadioButtonId) {
            case R.id.radioVis:
                type = "vis";
            break;
            case R.id.radioCur:
                type = "cur";
            break;
        }
        return type;
    }

}
