package com.bcabuddies.fitsteps;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPass extends AppCompatActivity {

    private TextInputEditText emailText;
    private String mEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_forgot_pass);

        emailText = findViewById(R.id.forgot_emailEditText);
        Button btnSubmit = findViewById(R.id.forgot_btnSubmit);
        auth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(v -> {

            if (Objects.requireNonNull(emailText.getText()).toString().equals("")) {
                Toast.makeText(ForgotPass.this, "PLease enter email", Toast.LENGTH_SHORT).show();
            } else {
                mEmail = emailText.getText().toString();
                auth.sendPasswordResetEmail(mEmail).addOnCompleteListener(task ->
                        Toast.makeText(ForgotPass.this, "Check your email inbox for password reset.", Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->
                        Toast.makeText(ForgotPass.this, "Failed. Try again.", Toast.LENGTH_SHORT).show());
            }
        });

    }
}
