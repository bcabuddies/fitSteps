package com.bcabuddies.fitsteps;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Steps extends AppCompatActivity implements SensorEventListener {

    TextView stepsCount;
    SensorManager sensorManager;
    Boolean running = false;
    Button btnFinish;
    Integer stepsValue = 0;
    FirebaseAuth firebaseAuth;
    String userId;
    double userWeight = 0, userHeight = 0;
    FirebaseFirestore firebaseFirestore;
    TextView calBurned, distCovered;
    Double caloriesBurnedPerMile, strip, stepCountMile, conversationFactor, distance;
    Integer caloriesBurned;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        stepsCount = findViewById(R.id.tv_steps);
        btnFinish = findViewById(R.id.btn_finish);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        calBurned = findViewById(R.id.tv_calories);
        distCovered = findViewById(R.id.tv_distance);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        editor = getSharedPreferences("my_pref", MODE_PRIVATE).edit();

        firebaseFirestore.collection("Users").document(userId).collection("user_data").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    userWeight = Double.valueOf(task.getResult().getString("weight"));
                    userHeight = Double.valueOf(task.getResult().getString("height"));
                }
            }
        });


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putInt("steps_value", 0);
                editor.apply();
                stepsCount.setText("0");

                Map<String, Object> map = new HashMap<>();
                map.put("steps", stepsValue.toString());
                map.put("calories", caloriesBurned.toString());
                map.put("distance", String.format("%.2f", distance) + " Km");
                map.put("time", FieldValue.serverTimestamp());
                map.put("uid", userId);
                firebaseFirestore.collection("RunData").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(Steps.this, "Finished Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Steps.this, "please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });

                // stepsCount.setText("0");
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
        stepsValue = prefs.getInt("steps_value", 0);
        Log.e("my_prefs", "onResume: " + prefs.getInt("steps_value", 0));
        stepsCount.setText(stepsValue.toString());
    }


    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        //sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {


            editor.putInt("steps_value", stepsValue);
            editor.apply();

            stepsCount.setText(stepsValue.toString());
            stepsValue = stepsValue + 1;
            caloriesBurnedPerMile = 0.57 * userWeight;
            strip = userHeight * 0.415;
            stepCountMile = 160934.4 / strip;
            conversationFactor = stepsValue / stepCountMile;
            caloriesBurned = (int) (stepsValue * conversationFactor);
            calBurned.setText(caloriesBurned.toString() + " cal");
            distance = (stepsValue * strip) / 100000;
            distCovered.setText(String.format("%.2f", distance) + " Km");


        } else {
            event.values[0] = 0;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
