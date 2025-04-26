package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private CheckBox userCheck, orphanageCheck;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userCheck = (CheckBox) findViewById(R.id.userButton);
        orphanageCheck = (CheckBox) findViewById(R.id.orphanageButton);

        register = (TextView) findViewById(R.id.signupButton);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.emailBox);
        editTextPassword = (EditText) findViewById(R.id.passwordBox);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupButton:
                startActivity(new Intent(this,RegisterUser.class));
                break;
            case R.id.loginButton:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid Email ID");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            editTextPassword.setError("Minimum 6 characters should be there!");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //redirect to user homepage
                    if(userCheck.isChecked()){
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                    }

                    //redirect to orphanage homepage
                    else if(orphanageCheck.isChecked()){
                        startActivity(new Intent(MainActivity.this, OrphanageProfile.class));
                    }
                    //if nothing checked go to user profile
                    else {
                        startActivity(new Intent(MainActivity.this, UserProfile.class));

                    }
                }
                else {
                    //show error
                    Toast.makeText(MainActivity.this, "Invalid credentials. Try Again!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}