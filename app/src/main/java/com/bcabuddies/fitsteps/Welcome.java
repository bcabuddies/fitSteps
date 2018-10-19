package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {


    private static final String TAG = "Welcome.java";
    private Window mWindow;
    private FirebaseAuth auth;
    private String email, password;
    private TextInputEditText userEmail, userPass;
    private Button btnLogin;
    TextView txtReg, txtForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.WelcomeAppTheme);
        mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        auth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.login_emailEditText);
        userPass = findViewById(R.id.login_passwordEditText);
        btnLogin = findViewById(R.id.login_loginBtn);
        txtReg = findViewById(R.id.login_register);
        txtForgotPass = findViewById(R.id.login_forgetPassword);


        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, RegisterMain.class));
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, ForgotPass.class));
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = userEmail.getText().toString();
                password = userPass.getText().toString();
                if (!(email.equals("") || password.equals(""))) {
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Welcome.this, "Sucessfully Logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Welcome.this,Home.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Welcome.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Welcome.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}
