package com.bcabuddies.fitsteps;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "Welcome.java";
    private LoginFrag loginFrag;
    private RegisterFrag registerFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginFrag = new LoginFrag();
        registerFrag = new RegisterFrag();

        initializeFragment();
    }

    private void initializeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.welcome_frameLayout, loginFrag);
        ft.add(R.id.welcome_frameLayout, registerFrag);

        ft.hide(registerFrag);
        ft.commit();
    }
}
