package com.liamgomez.cs454_shake_unlock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

// debug
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class ShakeListener extends Service implements SensorEventListener {

    private static final float SHAKE_GRAVITY_THRESH = 2.7F;
    private static final int RESET_INTERVAL_AFTER_MS = 3000;
    private static final int TIME_BETWEEN_SHAKES = 450;

    private final IBinder binder = new ShakeBinder();
    private ServiceCallbacks serviceCallbacks;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastShakeTime;
    private int numShakes;

    public ShakeListener() {
    }

    public class ShakeBinder extends Binder {
        ShakeListener getService() {
            return ShakeListener.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastShakeTime = System.currentTimeMillis();

        if (accelerometer == null) {
            Toast.makeText(this, "No accelerometer found!", Toast.LENGTH_SHORT).show();
        }
        else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, new Handler());
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
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        float xG = x / SensorManager.GRAVITY_EARTH;
        float yG = y / SensorManager.GRAVITY_EARTH;
        float zG = z / SensorManager.GRAVITY_EARTH;

        float force = (float) Math.sqrt((double) (xG * xG + yG * yG + zG * zG));

        if (force > SHAKE_GRAVITY_THRESH) {
            final long currentTimeMs = System.currentTimeMillis();

            if (lastShakeTime + TIME_BETWEEN_SHAKES > currentTimeMs) {
                return;
            }

            if (lastShakeTime + RESET_INTERVAL_AFTER_MS < currentTimeMs) {
                numShakes = 0;
            }

            Log.d(TAG, "Shake detected " + numShakes);
            lastShakeTime = currentTimeMs;
            numShakes++;
            serviceCallbacks.handleShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
