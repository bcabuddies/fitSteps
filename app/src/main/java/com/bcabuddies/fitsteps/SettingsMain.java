package com.bcabuddies.fitsteps;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsMain extends AppCompatActivity {

    private SettingsFrag settingsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
        settingsFrag=new SettingsFrag();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.add(R.id.settings_frame,settingsFrag);
        ft.commit();

    }
}
