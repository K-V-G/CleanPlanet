package com.example.cleanplanet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleanplanet.model.usersHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class authorizationView extends AppCompatActivity implements View.OnClickListener {
    ImageButton googleButton;
    ImageButton vkButton;
    ImageButton telegramButton;
    ImageButton facebookButton;
    EditText emailField;
    EditText passwordField;
    TextView userForgotPassword;
    TextView politics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_view);

        googleButton = findViewById(R.id.google);
        vkButton = findViewById(R.id.vk);
        telegramButton = findViewById(R.id.telegram);
        facebookButton = findViewById(R.id.facebook);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        userForgotPassword = findViewById(R.id.userForgotPassword);
        politics = findViewById(R.id.politics);

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

        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);
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

        TextInputLayout layout = (TextInputLayout) findViewById(R.id.passwordAuntificL);
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

        return valid;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerButton:
                Intent intent = new Intent(this, registrationView.class);
                startActivity(intent);
                break;
            case R.id.loginButton:
                signIn(emailField.getText().toString(), passwordField.getText().toString());
                break;
        }
    }

    private void signIn(String email, String password) {
        if (validateForm() == false) {
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(authorizationView.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(authorizationView.this, "Авторизация успешна", Toast.LENGTH_LONG).show();
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference(encodeUserEmail(email));
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            usersHelperClass user = snapshot.getValue(usersHelperClass.class);
                            if (user == null) {
                                Toast.makeText(authorizationView.this, "Что-то пошло не так", Toast.LENGTH_LONG).show();
                            }
                            else {
                                editor.putString("userName", user.getUserName());
                                editor.apply();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            System.out.println("The read failed: " + error.getCode());
                        }
                    });
                    Intent intent = new Intent(authorizationView.this, mainView.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(authorizationView.this, "Авторизация провалена", Toast.LENGTH_LONG).show();
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