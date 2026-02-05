# Build Instructions for Music Streamer AirPlay Receiver

## Overview

This skeleton provides a complete Android application structure for an AirPlay (RAOP) receiver that turns an Android 9+ device into a wireless speaker over its own hotspot.

## What's Included

### ✅ Complete Application Structure
- Android project with Gradle build configuration
- Manifest with all required permissions and service declarations
- Main Activity with UI controls
- Foreground service architecture
- All component implementations (HotspotController, MdnsAdvertiser, AudioEngine, RaopBridge)
- Native C++ JNI stub with CMake configuration
- Resources (layouts, strings, themes, icons)

### ⚠️ What's NOT Included
- **Android SDK** - Must be installed separately
- **Gradle dependencies** - Downloaded on first build
- **Native RAOP library** - Stub only; requires integration

## Prerequisites

### Required
1. **Android Studio** (Arctic Fox or later) - https://developer.android.com/studio
2. **Android SDK** with the following:
   - Android API 28 (Android 9.0) minimum
   - Android API 33 (Android 13) target
   - Build Tools 33.0.0+
3. **Android NDK** (for native code compilation)
4. **JDK 11** or later

### Optional (for native RAOP implementation)
- RAOP library source (e.g., Shairport-sync)
- OpenSSL for Android
- Cross-compilation tools

## Building the App

### Method 1: Android Studio (Recommended)

1. **Open Project**
   ```
   File > Open > Select project root directory
   ```

2. **Sync Gradle**
   - Android Studio will automatically sync Gradle files
   - Wait for dependency downloads (first time may take several minutes)

3. **Build**
   ```
   Build > Make Project
   ```
   Or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

4. **Run**
   - Connect Android device or start emulator
   - Click Run button or press `Shift+F10`

### Method 2: Command Line

1. **Setup Android SDK**
   ```bash
   export ANDROID_HOME=/path/to/android-sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

2. **Build Debug APK**
   ```bash
   cd /path/to/music-streamer-app
   ./gradlew assembleDebug
   ```

3. **Install on Device**
   ```bash
   ./gradlew installDebug
   ```

4. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```

## Expected Build Behavior

### ✅ What Should Work
- Project opens in Android Studio without errors
- Gradle sync completes successfully
- Java code compiles without errors
- C++ stub compiles successfully
- APK builds and installs on device
- App launches without crashing
- UI displays correctly
- Service can be started (creates hotspot)
- Service can be stopped
- mDNS advertising works
- No compilation or runtime errors

### ⚠️ What Won't Work (Without Native RAOP Library)
- **Actual AirPlay streaming** - Requires RAOP library integration
- Audio data reception from iOS devices
- PCM audio playback from AirPlay sources

The app will:
- Start successfully
- Create a LocalOnlyHotspot
- Advertise as an AirPlay device via mDNS
- Show as available in iOS AirPlay menu
- BUT: Won't receive or play audio streams

## Verification Steps

After building, verify the skeleton works:

1. **Install and Launch**
   - App installs without errors
   - App icon appears in launcher
   - App opens to main screen

2. **Grant Permissions**
   - Tap "Start Speaker Mode"
   - Grant location permission
   - Grant WiFi permission
   - Grant notification permission (Android 13+)

3. **Start Service**
   - Tap "Start Speaker Mode" again
   - Status changes to "Starting..." then "Running"
   - Notification appears showing "AirPlay Receiver running"
   - Hotspot SSID and password are displayed

4. **Test mDNS Discovery**
   - Connect iOS device to the hotspot
   - Open an app with AirPlay (Music, Videos, etc.)
   - Tap AirPlay icon
   - Device should appear in the list (e.g., "SM-G960F")

5. **Stop Service**
   - Tap "Stop Speaker Mode"
   - Status changes to "Stopped"
   - Hotspot turns off
   - Notification disappears

## Common Build Issues

### Issue: "SDK location not found"
**Solution:** Create `local.properties` file:
```properties
sdk.dir=/path/to/Android/Sdk
```

### Issue: "Failed to install NDK"
**Solution:** In Android Studio:
- Tools > SDK Manager > SDK Tools
- Check "NDK (Side by side)"
- Click Apply

### Issue: "CMake not found"
**Solution:** In Android Studio:
- Tools > SDK Manager > SDK Tools  
- Check "CMake"
- Click Apply

### Issue: Gradle sync fails
**Solution:**
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/
./gradlew clean
```

### Issue: JmDNS dependency not downloading
**Solution:** Check internet connection and:
```bash
./gradlew --refresh-dependencies
```

## Project Structure

```
music-streamer-app/
├── app/
│   ├── build.gradle                 # App-level Gradle config
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml  # App manifest
│   │       ├── java/                # Java source code
│   │       │   └── com/manoj077/musicstreamerapp/
│   │       │       ├── MainActivity.java
│   │       │       ├── ReceiverService.java
│   │       │       ├── HotspotController.java
│   │       │       ├── MdnsAdvertiser.java
│   │       │       ├── AudioEngine.java
│   │       │       └── RaopBridge.java
│   │       ├── cpp/                 # Native C++ code
│   │       │   ├── CMakeLists.txt
│   │       │   ├── raop_bridge.cpp
│   │       │   └── README.md
│   │       └── res/                 # Resources
│   │           ├── layout/
│   │           ├── values/
│   │           ├── drawable/
│   │           └── mipmap-*/
├── build.gradle                     # Project-level Gradle config
├── settings.gradle                  # Gradle settings
├── gradle.properties                # Gradle properties
└── README.md                        # Project README
```

## Compiles With

- **minSdkVersion:** 28 (Android 9.0)
- **targetSdkVersion:** 33 (Android 13)
- **compileSdkVersion:** 33
- **Gradle:** 8.0
- **Android Gradle Plugin:** 8.1.0
- **JDK:** 11+
- **NDK:** Any recent version

## Next Steps

To enable actual AirPlay functionality:

1. Read `app/src/main/cpp/README.md`
2. Integrate RAOP library (Shairport-sync recommended)
3. Implement native methods in `raop_bridge.cpp`
4. Build and test with iOS device

## License

This skeleton is provided as a starting point for development.
