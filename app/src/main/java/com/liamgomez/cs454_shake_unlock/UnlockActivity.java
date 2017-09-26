package com.liamgomez.cs454_shake_unlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UnlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        // start shake detection service
        Intent intent = new Intent(this, ShakeListener.class);
        startService(intent);
    }
}
