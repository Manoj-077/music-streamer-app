# Native RAOP Integration

This directory contains stub implementations for the RAOP (AirPlay) protocol.

## Current Status

The current implementation is a **STUB** that compiles but does not provide actual AirPlay functionality.
The app will build and run, and you can start/stop the service, but it won't receive audio streams.

## Integrating a Real RAOP Library

To enable actual AirPlay audio streaming, you need to integrate a RAOP library such as:
- **Shairport-sync**: https://github.com/mikebrady/shairport-sync
- **RPiPlay**: https://github.com/FD-/RPiPlay
- Other RAOP/AirPlay server implementations

### Integration Steps

1. **Add RAOP Library Source**
   - Clone or copy the RAOP library source into this directory
   - Example: `app/src/main/cpp/shairport-sync/`

2. **Update CMakeLists.txt**
   - Add RAOP library source files
   - Link required dependencies (OpenSSL, etc.)
   - Example:
     ```cmake
     add_subdirectory(shairport-sync)
     target_link_libraries(raop shairport-sync)
     ```

3. **Implement Native Methods**
   - In `raop_bridge.cpp`, initialize the RAOP server in `nativeStart`
   - Store the server instance and return its pointer as the handle
   - In `nativeStop`, cleanup and shutdown the server

4. **Handle Audio Callbacks**
   - When the RAOP library receives PCM audio data, call the Java callback:
   ```cpp
   void onAudioReceived(const uint8_t* pcmData, size_t length) {
       // Get JNIEnv from your stored JavaVM
       JNIEnv* env = getJNIEnv();
       
       // Call Java method onPcmFrame
       jbyteArray byteArray = env->NewByteArray(length);
       env->SetByteArrayRegion(byteArray, 0, length, (jbyte*)pcmData);
       env->CallVoidMethod(raopBridgeObject, onPcmFrameMethodID, 
                          byteArray, (jint)length);
       env->DeleteLocalRef(byteArray);
   }
   ```

5. **Build Dependencies**
   - Ensure all required libraries (e.g., OpenSSL) are available
   - May need to cross-compile dependencies for Android ABIs

## Current Stub Behavior

- `nativeStart()` logs parameters and returns 0 (indicating stub mode)
- `nativeStop()` does nothing
- No actual network protocol implementation
- Service will run without errors but won't receive AirPlay streams

## Testing

Once integrated, test with:
1. Start the app and enable Speaker Mode
2. Connect your iOS device to the hotspot created by the app
3. Open Apple Music or another app on iOS
4. Look for the device name in AirPlay menu
5. Stream audio to verify PCM data flows to AudioEngine
