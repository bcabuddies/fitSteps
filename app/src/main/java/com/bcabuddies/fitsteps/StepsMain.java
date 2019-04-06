package com.bcabuddies.fitsteps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.MenuItem;

import com.bcabuddies.fitsteps.StepsData.StepListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StepsMain extends AppCompatActivity  implements SensorEventListener, StepListener {

    private Fragment fragment = null;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_main);

        bottomNavigationView=findViewById(R.id.stepsmain_bottomtnavigation);
        fragment = StepsFrag.newInstance();
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.stepsmain_frame, fragment);
        fragmentTransaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.stepsmenu_activity:
                        fragment=StepsFrag.newInstance();
                        break;
                    case R.id.stepsmenu_profile:
                        fragment=ProfileFrag.newInstance();
                        break;
                }
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.stepsmain_frame, fragment);
                fragmentTransaction2.commit();
                return false;
            }
        });

    }

    @Override
    public void step(long timeNs) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
