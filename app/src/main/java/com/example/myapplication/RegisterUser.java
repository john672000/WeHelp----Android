package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private TextView registerUser, logIn;
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextAddress, edittextPhoneNumber;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.registeruser);
        registerUser.setOnClickListener(this);

        logIn = (TextView) findViewById(R.id.login);
        logIn.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.signupName);
        editTextEmail = (EditText) findViewById(R.id.signupEmail);
        editTextPassword = (EditText) findViewById(R.id.signupPassword);
        editTextAddress = (EditText) findViewById(R.id.signupAddress);
        edittextPhoneNumber = (EditText) findViewById(R.id.phoneNumber);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.registeruser:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String phoneNo = edittextPhoneNumber.getText().toString().trim();

        //Validation
        if(fullName.isEmpty()) {
            editTextFullName.setError("Full Name is required!");
            editTextFullName.requestFocus();
            return;
        }

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

        if (address.isEmpty()) {
            editTextAddress.setError("Address is required!");
            editTextAddress.requestFocus();
            return;
        }

        if (phoneNo.isEmpty()) {
            edittextPhoneNumber.setError("Phone no. is required!");
            edittextPhoneNumber.requestFocus();
            return;
        }

        if(!Patterns.PHONE.matcher(phoneNo).matches()) {
            edittextPhoneNumber.setError("Invalid");
            edittextPhoneNumber.requestFocus();
            return;
        }

        //Validation Ends here

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if user is registered
                        if(task.isSuccessful()) {
                            User user = new User(fullName, email, address, phoneNo);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // if data inserted to DataBase
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this,"Registration Successful. Login now!", Toast.LENGTH_LONG).show();
                                        // redirect user to homepage for login
                                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                    }
                                    else {
                                        Toast.makeText(RegisterUser.this,"Registration failed. Try Again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterUser.this,"Registration failed. Try Again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}














