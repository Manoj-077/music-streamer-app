package com.manoj077.musicstreamerapp;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioEngine {
    private static final String TAG = "AudioEngine";
    
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    
    private AudioTrack audioTrack;
    private BlockingQueue<byte[]> audioQueue;
    private Thread playbackThread;
    private volatile boolean isRunning = false;

    public AudioEngine() {
        audioQueue = new LinkedBlockingQueue<>(10); // Small jitter buffer
    }

    public void start() {
        if (isRunning) {
            Log.w(TAG, "AudioEngine already running");
            return;
        }

        Log.d(TAG, "Starting AudioEngine");
        
        // Calculate buffer size
        int minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
        );
        
        int bufferSize = minBufferSize * 2; // Use 2x min buffer for stability

        // Create AudioTrack with low-latency mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                    .build();

            AudioFormat audioFormat = new AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(CHANNEL_CONFIG)
                    .setEncoding(AUDIO_FORMAT)
                    .build();

            audioTrack = new AudioTrack(
                    audioAttributes,
                    audioFormat,
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
            );
        } else {
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
            );
        }

        audioTrack.play();
        isRunning = true;

        // Start playback thread
        playbackThread = new Thread(this::playbackLoop);
        playbackThread.start();
        
        Log.d(TAG, "AudioEngine started");
    }

    public void stop() {
        if (!isRunning) {
            Log.w(TAG, "AudioEngine not running");
            return;
        }

        Log.d(TAG, "Stopping AudioEngine");
        isRunning = false;

        if (playbackThread != null) {
            playbackThread.interrupt();
            try {
                playbackThread.join(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error joining playback thread", e);
            }
            playbackThread = null;
        }

        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }

        audioQueue.clear();
        Log.d(TAG, "AudioEngine stopped");
    }

    public void writePcmData(byte[] data) {
        if (!isRunning || data == null || data.length == 0) {
            return;
        }

        try {
            // Non-blocking add, drops if queue is full
            if (!audioQueue.offer(data)) {
                Log.w(TAG, "Audio queue full, dropping frame");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error writing PCM data", e);
        }
    }

    private void playbackLoop() {
        Log.d(TAG, "Playback loop started");
        
        while (isRunning) {
            try {
                byte[] data = audioQueue.take(); // Blocking wait for data
                
                if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    int written = audioTrack.write(data, 0, data.length);
                    if (written < 0) {
                        Log.e(TAG, "Error writing to AudioTrack: " + written);
                    }
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Playback loop interrupted");
                break;
            } catch (Exception e) {
                Log.e(TAG, "Error in playback loop", e);
            }
        }
        
        Log.d(TAG, "Playback loop ended");
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public int getChannels() {
        return 2; // Stereo
    }

    public int getBitDepth() {
        return 16;
    }
}
