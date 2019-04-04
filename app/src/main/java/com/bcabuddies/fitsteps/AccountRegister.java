package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class AccountRegister extends AppCompatActivity {


    private TextInputEditText userEmail, userPass, userCpass;
    private Button btnSubmit;
    private FirebaseAuth auth;
    private String email, password, confirmPass;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        userEmail = findViewById(R.id.regiter_emailEditText);
        userPass = findViewById(R.id.register_passEditText);
        userCpass = findViewById(R.id.register_CpassEditText);
        btnSubmit = findViewById(R.id.register_btnSubmit);
        auth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        btnSubmit.setOnClickListener(v -> {
            email = userEmail.getText().toString();
            password = userPass.getText().toString();
            confirmPass = userCpass.getText().toString();

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
