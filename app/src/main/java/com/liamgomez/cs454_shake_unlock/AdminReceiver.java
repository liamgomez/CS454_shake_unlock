package com.liamgomez.cs454_shake_unlock;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver{
    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Admin enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Admin Disabled");
    }
}
