package com.manoj077.musicstreamerapp;

import android.util.Log;

public class RaopBridge {
    private static final String TAG = "RaopBridge";
    
    private AudioEngine audioEngine;
    private long nativeHandle = 0;
    private boolean isStarted = false;

    static {
        try {
            System.loadLibrary("raop");
            Log.d(TAG, "Native RAOP library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native RAOP library not found - stub mode only", e);
        }
    }

    // Native methods - to be implemented in native library
    private native long nativeStart(int port, int sampleRate, int channels, int bitDepth);
    private native void nativeStop(long handle);

    public void start(AudioEngine audioEngine, int port) {
        if (isStarted) {
            Log.w(TAG, "RAOP bridge already started");
            return;
        }

        this.audioEngine = audioEngine;
        
        try {
            nativeHandle = nativeStart(
                    port,
                    audioEngine.getSampleRate(),
                    audioEngine.getChannels(),
                    audioEngine.getBitDepth()
            );
            
            if (nativeHandle != 0) {
                isStarted = true;
                Log.d(TAG, "RAOP bridge started on port " + port);
            } else {
                Log.w(TAG, "RAOP bridge failed to start (native library may not be available)");
            }
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "RAOP native methods not available - running in stub mode", e);
            // Continue in stub mode - service will still run but won't receive audio
            isStarted = true;
        }
    }

    public void stop() {
        if (!isStarted) {
            Log.w(TAG, "RAOP bridge not started");
            return;
        }

        try {
            if (nativeHandle != 0) {
                nativeStop(nativeHandle);
                nativeHandle = 0;
            }
            Log.d(TAG, "RAOP bridge stopped");
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "Error stopping RAOP native (stub mode)", e);
        }

        isStarted = false;
        audioEngine = null;
    }

    /**
     * Called from native code when PCM audio frame is received.
     * This is the callback that the native RAOP library will invoke.
     */
    @SuppressWarnings("unused") // Called from native code
    private void onPcmFrame(byte[] pcmData, int length) {
        if (audioEngine != null && pcmData != null && length > 0) {
            // If length is less than array size, copy only the valid portion
            if (length < pcmData.length) {
                byte[] validData = new byte[length];
                System.arraycopy(pcmData, 0, validData, 0, length);
                audioEngine.writePcmData(validData);
            } else {
                audioEngine.writePcmData(pcmData);
            }
        }
    }
}
