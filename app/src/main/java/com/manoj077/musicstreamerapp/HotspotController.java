package com.manoj077.musicstreamerapp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

public class HotspotController {
    private static final String TAG = "HotspotController";
    
    private Context context;
    private WifiManager wifiManager;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;

    public interface HotspotCallback {
        void onStarted(String ssid, String password);
        void onFailed(String error);
    }

    public HotspotController(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public void start(HotspotCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                    @Override
                    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                        super.onStarted(reservation);
                        Log.d(TAG, "LocalOnlyHotspot started");
                        hotspotReservation = reservation;
                        
                        String ssid = reservation.getWifiConfiguration().SSID;
                        String password = reservation.getWifiConfiguration().preSharedKey;
                        
                        if (callback != null) {
                            callback.onStarted(ssid, password);
                        }
                    }

                    @Override
                    public void onStopped() {
                        super.onStopped();
                        Log.d(TAG, "LocalOnlyHotspot stopped");
                    }

                    @Override
                    public void onFailed(int reason) {
                        super.onFailed(reason);
                        Log.e(TAG, "LocalOnlyHotspot failed: " + reason);
                        String errorMsg = "Failed to start hotspot (code: " + reason + ")";
                        
                        if (callback != null) {
                            callback.onFailed(errorMsg);
                        }
                    }
                }, null);
            } catch (SecurityException e) {
                Log.e(TAG, "Security exception starting hotspot", e);
                if (callback != null) {
                    callback.onFailed("Permission denied: " + e.getMessage());
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception starting hotspot", e);
                if (callback != null) {
                    callback.onFailed("Error: " + e.getMessage());
                }
            }
        } else {
            if (callback != null) {
                callback.onFailed("LocalOnlyHotspot requires Android 8.0+");
            }
        }
    }

    public void stop() {
        if (hotspotReservation != null) {
            try {
                hotspotReservation.close();
                hotspotReservation = null;
                Log.d(TAG, "Hotspot stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping hotspot", e);
            }
        }
    }
}
