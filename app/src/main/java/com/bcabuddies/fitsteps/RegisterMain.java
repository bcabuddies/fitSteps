package com.bcabuddies.fitsteps;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class RegisterMain extends AppCompatActivity {

    private RegisterSecondFrag registerSecondFrag;
    private Window mWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        setContentView(R.layout.activity_register_main);

        registerSecondFrag=new RegisterSecondFrag();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.add(R.id.registerMain_frame,registerSecondFrag);
        ft.commit();


    }
}
