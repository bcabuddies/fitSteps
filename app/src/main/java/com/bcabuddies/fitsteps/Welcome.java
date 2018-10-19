package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {


    private static final String TAG = "Welcome.java";
    private Window mWindow;

    private TextInputEditText userEmail,userPass;
    private Button btnLogin;
    TextView txtReg, txtForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        userEmail=findViewById(R.id.login_emailEditText);
        userPass=findViewById(R.id.login_passwordEditText);
        btnLogin=findViewById(R.id.login_loginBtn);
        txtReg=findViewById(R.id.login_register);
        txtForgotPass=findViewById(R.id.login_forgetPassword);

        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,RegisterMain.class));
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,ForgotPass.class));
            }
        });




    }
}
