package com.example.jamat_project_simulator;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show this screen using theme only
        try { Thread.sleep(2000); } catch (Exception e) {} // 2 seconds delay

        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
