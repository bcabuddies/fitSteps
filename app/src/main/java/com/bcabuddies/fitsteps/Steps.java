package com.bcabuddies.fitsteps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bcabuddies.fitsteps.StepsData.StepDetector;
import com.bcabuddies.fitsteps.StepsData.StepListener;

import androidx.appcompat.app.AppCompatActivity;

public class Steps extends AppCompatActivity implements SensorEventListener, StepListener {

    TextView stepsCount;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "";
    private int numSteps;
    Button btnFinish;
    Double caloriesBurnedPerMile, strip, stepCountMile, conversationFactor, distance;
    Integer caloriesBurned;
    int userWeight=70,userHeight=170;
    TextView calBurned, distCovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        stepsCount = findViewById(R.id.tv_steps);
        btnFinish = findViewById(R.id.btn_finish);
        calBurned = findViewById(R.id.tv_calories);
        distCovered = findViewById(R.id.tv_distance);

        numSteps = 0;
        sensorManager.registerListener(Steps.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(Steps.this);
            }
        });

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
        caloriesBurnedPerMile = 0.57 * userWeight;
        strip = userHeight * 0.415;
        stepCountMile = 160934.4 / strip;
        conversationFactor = numSteps / stepCountMile;
        caloriesBurned = (int) (numSteps * conversationFactor);
        calBurned.setText(caloriesBurned.toString() + " cal");
        distance = (numSteps * strip) / 100000;
        distCovered.setText(String.format("%.2f", distance) + " Km");
    }
}
