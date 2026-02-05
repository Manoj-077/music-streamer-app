#include <jni.h>
#include <android/log.h>
#include <string>

#define LOG_TAG "RaopBridge-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/**
 * Native RAOP Bridge Stub
 * 
 * This is a placeholder implementation for the RAOP (AirPlay) protocol.
 * To enable actual AirPlay functionality, integrate a full RAOP library
 * such as Shairport-sync or similar.
 * 
 * Expected integration:
 * 1. Add RAOP library source files to this directory
 * 2. Update CMakeLists.txt to include RAOP library
 * 3. Implement the protocol handling in nativeStart
 * 4. Call Java callback onPcmFrame when audio data is received
 * 
 * The native handle returned represents the RAOP server instance.
 */

extern "C" JNIEXPORT jlong JNICALL
Java_com_manoj077_musicstreamerapp_RaopBridge_nativeStart(
        JNIEnv* env,
        jobject thiz,
        jint port,
        jint sampleRate,
        jint channels,
        jint bitDepth) {
    
    LOGI("nativeStart called: port=%d, sampleRate=%d, channels=%d, bitDepth=%d",
         port, sampleRate, channels, bitDepth);
    
    // TODO: Initialize actual RAOP server here
    // For now, return a dummy handle to indicate stub mode
    // Return 0 to indicate failure (no actual implementation)
    
    LOGI("Native RAOP library is in stub mode - no actual implementation");
    LOGI("To enable AirPlay: integrate Shairport-sync or similar RAOP library");
    
    // Returning 0 to indicate stub mode / no implementation
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_manoj077_musicstreamerapp_RaopBridge_nativeStop(
        JNIEnv* env,
        jobject thiz,
        jlong handle) {
    
    LOGI("nativeStop called: handle=%ld", (long)handle);
    
    // TODO: Stop and cleanup RAOP server here
    
    if (handle == 0) {
        LOGI("Stub handle, nothing to stop");
        return;
    }
    
    LOGI("Native RAOP stopped (stub mode)");
}

/**
 * Example of how to call the Java callback when PCM data is received:
 * 
 * void sendPcmDataToJava(JNIEnv* env, jobject raopBridgeObj, 
 *                        const uint8_t* pcmData, size_t length) {
 *     jclass cls = env->GetObjectClass(raopBridgeObj);
 *     jmethodID mid = env->GetMethodID(cls, "onPcmFrame", "([BI)V");
 *     
 *     jbyteArray byteArray = env->NewByteArray(length);
 *     env->SetByteArrayRegion(byteArray, 0, length, (jbyte*)pcmData);
 *     
 *     env->CallVoidMethod(raopBridgeObj, mid, byteArray, (jint)length);
 *     
 *     env->DeleteLocalRef(byteArray);
 *     env->DeleteLocalRef(cls);
 * }
 */
