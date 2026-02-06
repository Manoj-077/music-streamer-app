package com.manoj077.musicstreamerapp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class MdnsAdvertiser {
    private static final String TAG = "MdnsAdvertiser";
    private static final String SERVICE_TYPE = "_airplay._tcp.local.";
    
    private Context context;
    private JmDNS jmdns;
    private WifiManager.MulticastLock multicastLock;

    public MdnsAdvertiser(Context context) {
        this.context = context;
    }

    public void start(String deviceName, int port) {
        new Thread(() -> {
            try {
                // Acquire multicast lock
                WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    multicastLock = wifiManager.createMulticastLock("AirPlayLock");
                    multicastLock.setReferenceCounted(true);
                    multicastLock.acquire();
                }

                // Get local IP address
                InetAddress localAddress = getLocalInetAddress();
                if (localAddress == null) {
                    Log.e(TAG, "Could not determine local IP address");
                    return;
                }

                // Create JmDNS instance
                jmdns = JmDNS.create(localAddress, deviceName);
                
                // Prepare service info with AirPlay TXT records
                HashMap<String, String> txtRecords = new HashMap<>();
                txtRecords.put("txtvers", "1");
                txtRecords.put("ch", "2");  // 2 channels (stereo)
                txtRecords.put("cn", "0,1");  // Codec: PCM
                txtRecords.put("et", "0,1");  // Encryption types
                txtRecords.put("md", "0,1,2");  // Metadata support
                txtRecords.put("pw", "false");  // No password required
                txtRecords.put("sr", "44100");  // Sample rate
                txtRecords.put("ss", "16");  // Sample size
                txtRecords.put("tp", "UDP");  // Transport protocol
                txtRecords.put("vn", "3");  // Version
                txtRecords.put("vs", "220.68");  // Server version
                txtRecords.put("am", "MusicStreamerApp");  // Model
                txtRecords.put("sf", "0x4");  // Features

                ServiceInfo serviceInfo = ServiceInfo.create(
                        SERVICE_TYPE,
                        deviceName,
                        port,
                        0,
                        0,
                        txtRecords
                );

                // Register service
                jmdns.registerService(serviceInfo);
                Log.d(TAG, "mDNS service registered: " + deviceName + " on port " + port);
                
            } catch (IOException e) {
                Log.e(TAG, "Error starting mDNS", e);
            }
        }).start();
    }

    public void stop() {
        new Thread(() -> {
            if (jmdns != null) {
                try {
                    jmdns.unregisterAllServices();
                    jmdns.close();
                    jmdns = null;
                    Log.d(TAG, "mDNS service stopped");
                } catch (IOException e) {
                    Log.e(TAG, "Error stopping mDNS", e);
                }
            }

            if (multicastLock != null && multicastLock.isHeld()) {
                multicastLock.release();
                multicastLock = null;
            }
        }).start();
    }

    private InetAddress getLocalInetAddress() {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                
                // Convert int IP to byte array
                byte[] ipBytes = new byte[4];
                ipBytes[0] = (byte) (ipAddress & 0xff);
                ipBytes[1] = (byte) ((ipAddress >> 8) & 0xff);
                ipBytes[2] = (byte) ((ipAddress >> 16) & 0xff);
                ipBytes[3] = (byte) ((ipAddress >> 24) & 0xff);
                
                return InetAddress.getByAddress(ipBytes);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting local IP", e);
        }
        return null;
    }
}
