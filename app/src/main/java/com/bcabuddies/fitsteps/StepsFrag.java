package com.bcabuddies.fitsteps;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bcabuddies.fitsteps.StepsData.StepDetector;
import com.bcabuddies.fitsteps.StepsData.StepListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import androidx.fragment.app.Fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepsFrag extends Fragment implements SensorEventListener, StepListener {

    TextView stepsCount;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "";
    private int numSteps;
    Button btnFinish;
    Double caloriesBurnedPerMile, strip, stepCountMile, conversationFactor, distance;
    Integer caloriesBurned;
    double userWeight = 0, userHeight = 0;
    TextView calBurned, distCovered;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String userId;
    private HashMap<String, Object> data;

    public StepsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        stepsCount = view.findViewById(R.id.tv_steps);
        btnFinish = view.findViewById(R.id.btn_finish);
        calBurned = view.findViewById(R.id.tv_calories);
        distCovered = view.findViewById(R.id.tv_distance);

        numSteps = 0;
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        data = new HashMap<>();

        firebaseFirestore.collection("Users").document(userId).collection("user_data").document(userId).get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                userWeight = Double.valueOf(task.getResult().getString("weight"));
                userHeight = Double.valueOf(task.getResult().getString("height"));
            }
        });

        btnFinish.setOnClickListener(v -> {
            Log.e(TAG, "onClick: finished");
            unregisterSensor();
            data.put("time", FieldValue.serverTimestamp());
            data.put("uid", userId);

            finish(data);
        });
        return view;
    }

    private void finish(HashMap<String, Object> data) {
        Log.e(TAG, "finish: data " + data);

        //check internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Log.e(TAG, "finish: internet available ");
            uploadData(data);
        } else {
            Log.e(TAG, "finish: no internet");
            saveData(data);
        }

    }

    private void saveData(HashMap<String, Object> data) {
        //save data for later upload
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("pendingData.data");
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(data);
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveData: exception "+e.getMessage() );
        }
    }

    private void uploadData(HashMap<String, Object> data) {
        //upload data to firebase
        firebaseFirestore.collection("RunData").add(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "uploadData: data uploaded ");
            } else {
                Log.e(TAG, "uploadData: error " + task.getException().getMessage());
                Toast.makeText(getContext(), "Error uploading data", Toast.LENGTH_SHORT).show();
                //save data for future upload
                saveData(data);
            }
        });
    }

    private void unregisterSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        stepsCount.setText(TEXT_NUM_STEPS + numSteps);
        data.put("steps", TEXT_NUM_STEPS + numSteps);

        caloriesBurnedPerMile = 0.57 * userWeight;
        strip = userHeight * 0.415;
        stepCountMile = 160934.4 / strip;
        conversationFactor = numSteps / stepCountMile;
        caloriesBurned = (int) (numSteps * conversationFactor);
        calBurned.setText(caloriesBurned.toString() + " cal");
        data.put("calories", caloriesBurned.toString() + " cal");

        distance = (numSteps * strip) / 100000;
        distCovered.setText(String.format("%.2f", distance) + " Km");
        data.put("distance", String.format("%.2f", distance) + " Km");
    }

    public static Fragment newInstance() {
        StepsFrag fragment = new StepsFrag();
        return fragment;

    }
}
