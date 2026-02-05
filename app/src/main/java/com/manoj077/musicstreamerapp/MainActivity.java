package com.manoj077.musicstreamerapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;

    private Button startButton;
    private Button stopButton;
    private TextView statusText;
    private TextView hotspotInfoText;
    private TextView passwordInfoText;
    private TextView airplayNameText;

    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        statusText = findViewById(R.id.statusText);
        hotspotInfoText = findViewById(R.id.hotspotInfoText);
        passwordInfoText = findViewById(R.id.passwordInfoText);
        airplayNameText = findViewById(R.id.airplayNameText);

        startButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                startReceiverService();
            } else {
                requestPermissions();
            }
        });

        stopButton.setOnClickListener(v -> stopReceiverService());
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ReceiverService.ACTION_STATUS_UPDATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(statusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(statusReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
    }

    private boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startReceiverService();
            } else {
                Toast.makeText(this, "Permissions required for Speaker Mode", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startReceiverService() {
        Intent intent = new Intent(this, ReceiverService.class);
        intent.setAction(ReceiverService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopReceiverService() {
        Intent intent = new Intent(this, ReceiverService.class);
        intent.setAction(ReceiverService.ACTION_STOP);
        startService(intent);
    }

    private void updateUI(Intent intent) {
        String status = intent.getStringExtra(ReceiverService.EXTRA_STATUS);
        String ssid = intent.getStringExtra(ReceiverService.EXTRA_SSID);
        String password = intent.getStringExtra(ReceiverService.EXTRA_PASSWORD);
        String airplayName = intent.getStringExtra(ReceiverService.EXTRA_AIRPLAY_NAME);

        if (status != null) {
            statusText.setText("Status: " + status);
            
            if (status.equals("Running")) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                
                if (ssid != null) {
                    hotspotInfoText.setText(getString(R.string.hotspot_info, ssid));
                    hotspotInfoText.setVisibility(View.VISIBLE);
                }
                if (password != null) {
                    passwordInfoText.setText(getString(R.string.password_info, password));
                    passwordInfoText.setVisibility(View.VISIBLE);
                }
                if (airplayName != null) {
                    airplayNameText.setText(getString(R.string.airplay_name, airplayName));
                    airplayNameText.setVisibility(View.VISIBLE);
                }
            } else {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                hotspotInfoText.setVisibility(View.GONE);
                passwordInfoText.setVisibility(View.GONE);
                airplayNameText.setVisibility(View.GONE);
            }
        }
    }
}
