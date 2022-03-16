package com.example.locationandsymptommonitoringapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

/**
 * Accelerometer service is used to sense or detect the motions and calculate the rate. It calculates based on the x, y and z indexes of the event
 */
public class AccelerometerSensingService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor senseAcceleration;
    private ArrayList<Integer> motionValuesX = new ArrayList<>();
    private ArrayList<Integer> motionValuesY = new ArrayList<>();
    private ArrayList<Integer> motionValuesZ = new ArrayList<>();

    @Override
    public void onCreate(){

        Log.i("log", "Resp Service started - Entered onCreate");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Registers the eventListener for the given sensor at the given frequency and the given latency.
        sensorManager.registerListener(this, senseAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("log", "Resp Service started - Exited onCreate");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.i("log", "Entered onSensorChanged");

        Sensor basicSensor = sensorEvent.sensor;
        if (basicSensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // To convert to integers and store the data
            motionValuesX.add((int)(sensorEvent.values[0] * 100));
            motionValuesY.add((int)(sensorEvent.values[1] * 100));
            motionValuesZ.add((int)(sensorEvent.values[2] * 100));

            //To Stop sensing after 45 seconds
            if(motionValuesX.size() >= 230){
                stopSelf();
            }

        }

        Log.i("log", "Exited onSensorChanged");
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("log", "Entered onStartCommand");
        motionValuesX.clear();
        motionValuesY.clear();
        motionValuesZ.clear();
        Log.i("log", "Exited onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy(){

        Log.i("log", "Entered onDestroy");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sensorManager.unregisterListener(AccelerometerSensingService.this);
                Log.i("log", "Service stopping");

                Intent intent = new Intent("broadcastingAccelData");
                Bundle b = new Bundle();
                b.putIntegerArrayList("accelValuesX", motionValuesX);
                intent.putExtras(b);
                LocalBroadcastManager.getInstance(AccelerometerSensingService.this).sendBroadcast(intent);
            }
        });
        thread.start();
        Log.i("log", "Exited onDestroy");
    }
}
