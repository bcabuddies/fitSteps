package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class AccountRegister extends AppCompatActivity {
    private TextInputEditText userEmail, userPass, userCPass;
    private FirebaseAuth auth;
    private String email, password, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        userEmail = findViewById(R.id.regiter_emailEditText);
        userPass = findViewById(R.id.register_passEditText);
        userCPass = findViewById(R.id.register_CpassEditText);
        Button btnSubmit = findViewById(R.id.register_btnSubmit);
        auth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(v -> {
            email = Objects.requireNonNull(userEmail.getText()).toString();
            password = Objects.requireNonNull(userPass.getText()).toString();
            confirmPass = Objects.requireNonNull(userCPass.getText()).toString();

            if (email.equals("") || password.equals("")) {
                Toast.makeText(AccountRegister.this, "Please. fill all info", Toast.LENGTH_SHORT).show();
            } else {
                if (password.equals(confirmPass)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                        Toast.makeText(AccountRegister.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AccountRegister.this, PostRegisterFirst.class));
                    });
                } else {
                    Toast.makeText(AccountRegister.this, "Password did not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
