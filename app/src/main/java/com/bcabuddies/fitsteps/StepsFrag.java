package com.bcabuddies.fitsteps;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bcabuddies.fitsteps.StepsData.StepDetector;
import com.bcabuddies.fitsteps.StepsData.StepListener;
import com.bcabuddies.fitsteps.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.bcabuddies.fitsteps.App.CHANNEL_1_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepsFrag extends Fragment implements SensorEventListener, StepListener {

    private TextView stepsCount;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private static final String TEXT_NUM_STEPS = "";
    private int numSteps = 0;
    private double userHeight = 0;
    private TextView calBurned, distCovered;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private HashMap<String, Object> data;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private NotificationManagerCompat notificationManager;
    private String distanceCovered, calBurn;
    private Boolean finishCheck = true;

    public StepsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        context = getContext();

        // Get an instance of the SensorManager
        getActivity();
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        notificationManager = NotificationManagerCompat.from(getActivity());
        showNotification();


        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        stepsCount = view.findViewById(R.id.tv_steps);
        Button btnFinish = view.findViewById(R.id.btn_finish);
        calBurned = view.findViewById(R.id.tv_calories);
        distCovered = view.findViewById(R.id.tv_distance);

        numSteps = 0;
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        data = new HashMap<>();

        firebaseFirestore.collection("Users").document(userId).collection("user_data").document(userId).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).exists()) {
                //Double userWeight = Double.valueOf(Objects.requireNonNull(task.getResult().getString("weight")));
                userHeight = Double.valueOf(Objects.requireNonNull(task.getResult().getString("height")));
            }
        });

        btnFinish.setOnClickListener(v -> {
            Log.e(TAG, "onClick: finished");
            unregisterSensor();
            Date currentTime = Calendar.getInstance().getTime();
            data.put("time", currentTime);
            data.put("uid", userId);
            finishBtn(data);
        });
        return view;
    }

    private void showNotification() {
        RemoteViews collapsedView = new RemoteViews(Objects.requireNonNull(getActivity()).getPackageName(),
                R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notification_expanded);

        collapsedView.setTextViewText(R.id.notif_dataTV, "Steps: " + numSteps + " Cal: " + calBurn + " Km: " + distanceCovered);
        expandedView.setTextViewText(R.id.notif_dataTV, "Steps: " + numSteps + "\nCalories: " + calBurn + "\nDistance: " + distanceCovered);

        Intent finishButton = new Intent("com.bcabuddies.fitsteps.FINISH_ACTION");
        IntentFilter intentFilter = new IntentFilter("com.bcabuddies.fitsteps.FINISH_ACTION");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (finishCheck) {
                    Log.e(TAG, "onReceive: finishClick");
                    unregisterSensor();
                    Date currentTime = Calendar.getInstance().getTime();
                    data.put("time", currentTime);
                    data.put("uid", userId);
                    finishBtn(data);
                    finishCheck=false;
                }
            }
        };

        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        PendingIntent finishBtnPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, finishButton, 0);
        expandedView.setOnClickPendingIntent(R.id.notifexpanded_btnfinish, finishBtnPendingIntent);

        Intent exitButton = new Intent(getActivity(), notificationReceiver.class).setAction("com.bcabuddies.fitsteps.CANCEL_ACTION");
        PendingIntent exitBtnPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, exitButton, 0);
        expandedView.setOnClickPendingIntent(R.id.notifexpanded_btnexit, exitBtnPendingIntent);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.logo)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
        notificationManager.notify(1, notification);
    }

    private void finishBtn(HashMap<String, Object> data) {
        Log.e(TAG, "finish: data " + data);

        //check internet
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Log.e(TAG, "finish: internet available ");
            uploadData(data);
        } else {
            Log.e(TAG, "finish: no internet");
            //saving data
            Utils.saveData(data, context);
            clearData();
        }

    }

    private void uploadData(HashMap<String, Object> data) {
        //upload data to firebase
        firebaseFirestore.collection("RunData").add(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "uploadData: data uploaded ");
                clearData();
            } else {
                Log.e(TAG, "uploadData: error " + Objects.requireNonNull(task.getException()).getMessage());
                Toast.makeText(getContext(), "Error uploading data", Toast.LENGTH_SHORT).show();
                //save data for future upload
                Utils.saveData(data, context);
                clearData();
            }
        });
    }

    private void clearData() {
        Intent i = new Intent(getContext(), StepsMain.class);
        startActivity(i);
        Objects.requireNonNull(getActivity()).finish();
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void step(long timeNs) {
        numSteps++;
        stepsCount.setText(TEXT_NUM_STEPS + numSteps);
        data.put("steps", TEXT_NUM_STEPS + numSteps);

        Double strip = userHeight * 0.415;
        Double stepCountMile = 160934.4 / strip;
        Double conversationFactor = numSteps / stepCountMile;
        int caloriesBurned = (int) (numSteps * conversationFactor);
        calBurned.setText(Integer.toString(caloriesBurned) + " cal");
        data.put("calories", Integer.toString(caloriesBurned) + " cal");
        calBurn = Integer.toString(caloriesBurned);

        Double distance = (numSteps * strip) / 100000;
        distCovered.setText(String.format("%.2f", distance) + " Km");
        data.put("distance", String.format("%.2f", distance) + " Km");
        distanceCovered = String.format("%.2f", distance);

        showNotification();

    }

    static Fragment newInstance() {
        return new StepsFrag();
    }
}
