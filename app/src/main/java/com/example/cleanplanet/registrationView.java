package com.example.cleanplanet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cleanplanet.model.usersHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class registrationView extends AppCompatActivity implements View.OnClickListener {

    EditText userNameField;
    EditText emailField;
    EditText passwordField;
    EditText repeatPasswordField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_view);

        userNameField = findViewById(R.id.userName);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        repeatPasswordField = findViewById(R.id.repeatPassword);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };


        findViewById(R.id.registrationButton).setOnClickListener(this);
        findViewById(R.id.comeBackToLogin).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registrationButton:
                createAcc(emailField.getText().toString(), passwordField.getText().toString(),
                        userNameField.getText().toString());
                break;
            case R.id.comeBackToLogin:
                Intent intent = new Intent(this, authorizationView.class);
                startActivity(intent);
                break;
        }
    }
   private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Поле не должно быть пустым");
            valid = false;
        }
        else {
            emailField.setError(null);
        }
        TextInputLayout layout = (TextInputLayout) findViewById(R.id.passwordL);
        layout.setError(null);
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            layout.setError("Поле не должно быть пустым");
            layout.setErrorIconDrawable(null);
            valid = false;
        }
        else if (password.length() < 6){
            layout.setError("Пароль должен содержать 6 и более знаков");
            layout.setErrorIconDrawable(null);
            valid = false;
        }
        else {
            passwordField.setError(null);
            layout.setErrorEnabled(false);
        }
       TextInputLayout layout1 = (TextInputLayout) findViewById(R.id.repeatPasswordL);
       layout1.setError(null);
        String repeatPassword = repeatPasswordField.getText().toString();
        if (!TextUtils.isEmpty(repeatPassword)) {
            if (!repeatPassword.equals(password)) {
                layout1.setError("Пароли должны совпадать!");
                layout1.setErrorIconDrawable(null);
                valid = false;
            }
        }
        else if (TextUtils.isEmpty(repeatPassword)) {
            layout1.setError("Поле не должно быть пустым");
            layout1.setErrorIconDrawable(null);
            valid = false;
        }
        else {
            repeatPasswordField.setError(null);
            layout1.setErrorEnabled(false);
        }

        String userName = userNameField.getText().toString();
       if (TextUtils.isEmpty(userName)) {
           emailField.setError("Поле не должно быть пустым");
           valid = false;
       }
       else {
           emailField.setError(null);
       }
        return valid;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createAcc(String email, String password, String userName) {
        if (validateForm() == false) {
            return;
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(encodeUserEmail(email));
        Clock clock = Clock.system(ZoneId.of("UTC+07"));
        LocalDateTime localDateTime = LocalDateTime.now(clock);

        usersHelperClass userInfo = new usersHelperClass(userName, localDateTime.toString(), true);
        reference.setValue(userInfo);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(registrationView.this, "Регистрация успешна", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(registrationView.this, authorizationView.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(registrationView.this, "Регистрация провалена", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }


}