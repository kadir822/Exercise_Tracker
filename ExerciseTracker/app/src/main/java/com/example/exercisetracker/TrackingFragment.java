package com.example.exercisetracker;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TrackingFragment extends Fragment implements SensorEventListener {
    final short POLL_FREQUENCY = 200; //in milliseconds
    private long lastUpdate = -1;
    private SensorManager sensorManager;
    Sensor sensor;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor gravity;
    private Sensor magnetic;

    MainActivity mainActivity;
    TextView gravityY;
    TextView currentExerciseField;


    float[] accelerometerMatrix = new float[3];
    float[] accelerometerWorldMatrix = new float[3];
    float[] gyroscopeMatrix = new float[3];
    float[] gravityMatrix = new float[3];
    float[] magneticMatrix = new float[3];
    float[] rotationMatrix = new float[9];


    int switchStateTracker = 0;
    int pushupCounter = 0;
    float lastLowPoint = 0;
    String currentExercise;

    int timer = 0;

    public TrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        //Set the nav drawer item highlight
        mainActivity = (MainActivity)getActivity();

        //Set actionbar title
        mainActivity.setTitle("Raw Data");

        //Sensor manager
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(MainActivity.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(MainActivity.TYPE_GYROSCOPE);
        gravity = sensorManager.getDefaultSensor(MainActivity.TYPE_GRAVITY);
        magnetic = sensorManager.getDefaultSensor(MainActivity.TYPE_MAGNETIC);

        //Get text fields
        gravityY = (TextView) view.findViewById(R.id.raw_value_grav_y);
        gravityY.setText("");
        currentExerciseField = (TextView) view.findViewById(R.id.currentExercise);
        currentExerciseField.setText("");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensor = event.sensor;

        timer++;

        int i = sensor.getType();
        if (i == MainActivity.TYPE_ACCELEROMETER) {
            accelerometerMatrix = event.values;
        } else if (i == MainActivity.TYPE_GYROSCOPE) {
            gyroscopeMatrix = event.values;
        } else if (i == MainActivity.TYPE_GRAVITY) {
            gravityMatrix = event.values;
        } else if (i == MainActivity.TYPE_MAGNETIC) {
            magneticMatrix = event.values;
        }

        switch(switchStateTracker){
            case(0):

                findLowPoint(gravityMatrix[1]);
                break;
            case(1):
                findHighPoint(gravityMatrix[1]);
                break;
            case(2):
                confirmPushup(gravityMatrix[1]);
                break;
            default:
                break;
        }
        Log.d("Timer:", ""+switchStateTracker);

        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - lastUpdate);

        // only allow one update every POLL_FREQUENCY.
        if(diffTime > POLL_FREQUENCY) {
            lastUpdate = curTime;

            SensorManager.getRotationMatrix(rotationMatrix, null, gravityMatrix, magneticMatrix);

            currentExerciseField.setText(currentExercise);
            gravityY.setText("" + pushupCounter);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //safe not to implement
    }

    public void findLowPoint(float lastIndex){
        timer = 0;

            if (lastIndex > 8f && lastIndex < 11f) {
                //gravYvalues = (ArrayList<Float>) gravYvalues.subList(gravYvalues.indexOf(lastIndex), gravYvalues.indexOf(gravYvalues.get(gravYvalues.size())));
                switchStateTracker = 1;
                lastLowPoint = lastIndex;
            }

    }

    public void findHighPoint(float lastIndex){

            if (lastIndex > 0f && lastIndex < 4f  ){
              switchStateTracker = 2;
            }
            else if (lastIndex > lastLowPoint || timer > 2000){
                switchStateTracker = 0;
            }

    }

    public void confirmPushup(float lastIndex){


            Log.d("Confirm pushup", "confirmed" + lastIndex);

            if ((lastIndex > 8f && lastIndex < 11f) && (timer < 2000 && timer > 300)){
                currentExercise = "Push-Ups:";
                pushupCounter++;
                switchStateTracker = 0;
            }
            else if ( timer > 2000){
                switchStateTracker = 0;
            }
    }

}
