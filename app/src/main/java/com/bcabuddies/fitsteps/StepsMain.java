package com.bcabuddies.fitsteps;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcabuddies.fitsteps.StepsData.StepListener;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

public class StepsMain extends AppCompatActivity implements SensorEventListener, StepListener {

    private static final String TAG = "stepsmain";
    private Fragment fragment = null;
    BottomNavigationView bottomNavigationView;
    private DrawerLayout sideDrawerLayout;
    ImageView toggleNavigation;
    NavigationView sideNavigationView;
    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    private String fullName;
    private String thumbUrl;
    private CircleImageView thumb_image;
    private TextView name;
    String userId;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_main);


        bottomNavigationView = findViewById(R.id.stepsmain_bottomtnavigation);
        sideDrawerLayout = findViewById(R.id.stepsmain_drawer);
        toggleNavigation = findViewById(R.id.home_toolbar_menuBtn);
        toggleNavigation = findViewById(R.id.home_toolbar_menuBtn);
        sideNavigationView = findViewById(R.id.stepsNav_view);
        toolbarTitle = findViewById(R.id.home_toolbar_titleTV);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        toolbarTitle.setText("Activity");
        fragment = StepsFrag.newInstance();
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.stepsmain_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //side nav bar
        toggleNavigation.setOnClickListener(v -> sideDrawerLayout.openDrawer(Gravity.LEFT));

        //check pending data upload
        checkDataUpload();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.stepsmenu_activity:
                    fragment = StepsFrag.newInstance();
                    toolbarTitle.setText("Activity");
                    break;
                case R.id.stepsmenu_profile:
                    fragment = ProfileFrag.newInstance();
                    toolbarTitle.setText("Profile");
                    break;
            }
            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.stepsmain_frame, fragment);
            fragmentTransaction2.addToBackStack(null);
            fragmentTransaction2.commit();
            return false;
        });

        sideNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_settings:
                    startActivity(new Intent(StepsMain.this, SettingsMain.class));
                    break;
                case R.id.menu_logout:
                    auth.signOut();
                    startActivity(new Intent(StepsMain.this, Welcome.class));
                    finish();
                    break;

            }
            return true;
        });

        //loading thumb image and full name in side navbar
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).exists()) {

                thumbUrl = task.getResult().getString("thumb_id");
                fullName = task.getResult().getString("name");
                // name.setText(fullName);
                thumb_image = findViewById(R.id.homeNav_thumbImage);
                name = findViewById(R.id.homeNav_name);
                Log.e(TAG, "onComplete: thumb: " + thumbUrl + "\n name: " + fullName);
                try {
                    Log.e(TAG, "try");
                    name.setText(fullName);
                    Glide.with(this).load(thumbUrl).into(thumb_image);
                } catch (Exception e) {
                    Log.e("dpName", "catch");
                }
            } else {

                Toast.makeText(this, "user not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Some error has occured", Toast.LENGTH_SHORT).show());
    }

    private void checkDataUpload() {
        //check for pending data upload
        try {
            String filePath = getFilesDir().getPath() + "/pendingList.data";
            File f = new File(filePath);
            FileInputStream fileInputStream  = new FileInputStream(f);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            list.addAll((Collection<? extends HashMap<String, Object>>) objectInputStream.readObject());
            objectInputStream.close();

            Log.e(TAG, "checkDataUpload: data "+list );

            if (!list.isEmpty()){
                for (HashMap<String, Object> l : list){
                    firebaseFirestore.collection("RunData").add(l).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "uploadData: data uploaded ");
                            String dir = getFilesDir().getPath() + "/pendingList.data";
                            File file = new File(dir);
                            if (file.delete()){
                                Log.e(TAG, "checkDataUpload: backup file deleted " );
                            }
                        } else {
                            Log.e(TAG, "uploadData: error " + task.getException().getMessage());
                        }
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "checkDataUpload: exception "+e.getMessage() );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "checkDataUpload: exception "+e.getMessage() );
        }
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
