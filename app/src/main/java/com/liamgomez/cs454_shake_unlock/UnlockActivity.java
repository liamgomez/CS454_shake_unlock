package com.liamgomez.cs454_shake_unlock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class UnlockActivity extends Activity implements ServiceCallbacks {

    private static final int REQUEST_CODE_ENABLE_ADMIN = 15;
    private ShakeListener shakeService;
    private boolean bound = false;

    PowerManager powerManager;
    private DevicePolicyManager policyManager;
    private ComponentName adminReciever;
    private KeyguardManager keyGaurd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        policyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyGaurd = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        adminReciever = new ComponentName(this, AdminReceiver.class);
        if (! policyManager.isAdminActive(adminReciever)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReciever);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, adminReciever);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // start shake detection service
        Intent intent = new Intent(this, ShakeListener.class);
        startService(intent);
        bindService(intent, shakeServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private ServiceConnection shakeServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            ShakeListener.ShakeBinder binder = (ShakeListener.ShakeBinder) service;
            shakeService = binder.getService();
            bound = true;
            shakeService.setCallbacks(UnlockActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void handleShake() {
        if (powerManager.isInteractive()) {
            Log.d(TAG, "Device Lock");
            policyManager.lockNow();
        }
        else {
            Log.d(TAG, "Device wake / unlock");
            PowerManager.WakeLock wake = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wake.acquire();

            if (wake.isHeld()) {
                wake.release();
            }

            if (keyGaurd.inKeyguardRestrictedInputMode()) {
                KeyguardManager.KeyguardLock keyguardLock = keyGaurd.newKeyguardLock(getLocalClassName());
                keyguardLock.disableKeyguard();
            }
        }
    }
}
