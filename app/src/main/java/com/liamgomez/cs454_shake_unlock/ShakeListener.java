package com.liamgomez.cs454_shake_unlock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

// debug
import android.util.Log;

public class ShakeListener extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float calculatedAcceleration;
    private float currentAcceleration;
    private float lastAcceleration;

    public ShakeListener() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null) {
            // do something no accelerometer
        }
        else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, new Handler());
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));

        float difference = currentAcceleration - lastAcceleration;

        calculatedAcceleration = calculatedAcceleration * 0.9f + difference;

        if (calculatedAcceleration > 11) {
            Log.d("SHAKE LISTENER : ", "DEVICE SHOOK.....");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
