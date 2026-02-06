package com.manoj077.musicstreamerapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReceiverService extends Service {
    private static final String TAG = "ReceiverService";
    private static final String CHANNEL_ID = "AirPlayReceiverChannel";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_START = "com.manoj077.musicstreamerapp.ACTION_START";
    public static final String ACTION_STOP = "com.manoj077.musicstreamerapp.ACTION_STOP";
    public static final String ACTION_STATUS_UPDATE = "com.manoj077.musicstreamerapp.ACTION_STATUS_UPDATE";

    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_SSID = "ssid";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_AIRPLAY_NAME = "airplay_name";

    private HotspotController hotspotController;
    private MdnsAdvertiser mdnsAdvertiser;
    private AudioEngine audioEngine;
    private RaopBridge raopBridge;

    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        
        hotspotController = new HotspotController(this);
        mdnsAdvertiser = new MdnsAdvertiser(this);
        audioEngine = new AudioEngine();
        raopBridge = new RaopBridge();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            startSpeakerMode();
        } else if (ACTION_STOP.equals(action)) {
            stopSpeakerMode();
        }

        return START_STICKY;
    }

    private void startSpeakerMode() {
        if (isRunning) {
            Log.d(TAG, "Speaker mode already running");
            return;
        }

        Log.d(TAG, "Starting speaker mode");
        
        // Create notification channel for Android O and above
        createNotificationChannel();
        
        // Start foreground service
        Notification notification = createNotification("Starting AirPlay receiver...");
        startForeground(NOTIFICATION_ID, notification);
        
        broadcastStatus("Starting");

        // Start hotspot
        hotspotController.start(new HotspotController.HotspotCallback() {
            @Override
            public void onStarted(String ssid, String password) {
                Log.d(TAG, "Hotspot started: " + ssid);
                onHotspotReady(ssid, password);
            }

            @Override
            public void onFailed(String error) {
                Log.e(TAG, "Hotspot failed: " + error);
                stopSpeakerMode();
            }
        });
    }

    private void onHotspotReady(String ssid, String password) {
        // Start audio engine
        audioEngine.start();
        
        // Start RAOP bridge (JNI stub)
        raopBridge.start(audioEngine, 5000); // Port 5000 for RAOP
        
        // Start mDNS advertising
        String deviceName = Build.MODEL.replaceAll("\\s+", "-");
        mdnsAdvertiser.start(deviceName, 5000);
        
        isRunning = true;
        
        // Update notification
        Notification notification = createNotification("AirPlay receiver running");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
        
        // Broadcast status
        Intent statusIntent = new Intent(ACTION_STATUS_UPDATE);
        statusIntent.putExtra(EXTRA_STATUS, "Running");
        statusIntent.putExtra(EXTRA_SSID, ssid);
        statusIntent.putExtra(EXTRA_PASSWORD, password);
        statusIntent.putExtra(EXTRA_AIRPLAY_NAME, deviceName);
        sendBroadcast(statusIntent);
    }

    private void stopSpeakerMode() {
        if (!isRunning) {
            Log.d(TAG, "Speaker mode not running");
            stopSelf();
            return;
        }

        Log.d(TAG, "Stopping speaker mode");
        
        // Stop RAOP
        raopBridge.stop();
        
        // Stop mDNS
        mdnsAdvertiser.stop();
        
        // Stop audio engine
        audioEngine.stop();
        
        // Stop hotspot
        hotspotController.stop();
        
        isRunning = false;
        
        broadcastStatus("Stopped");
        
        stopForeground(true);
        stopSelf();
    }

    private void broadcastStatus(String status) {
        Intent statusIntent = new Intent(ACTION_STATUS_UPDATE);
        statusIntent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(statusIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "AirPlay Receiver",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("AirPlay receiver service");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AirPlay Receiver")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        
        if (isRunning) {
            stopSpeakerMode();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
