package com.bcabuddies.fitsteps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    private TextInputEditText emailText;
    private Button btnSubmit;
    private Window mWindow;
    private String mEmail;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_forgot_pass);

        emailText = findViewById(R.id.forgot_emailEditText);
        btnSubmit = findViewById(R.id.forgot_btnSubmit);
        auth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emailText.getText().toString().equals("")) {
                    Toast.makeText(ForgotPass.this, "PLease enter email", Toast.LENGTH_SHORT).show();
                } else {
                    mEmail = emailText.getText().toString();
                    auth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ForgotPass.this, "Check your email inobx for password reset.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ForgotPass.this, "Failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
