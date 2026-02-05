# Music Streamer App

An Android application that turns an old Android 9+ phone into a wireless AirPlay (RAOP) receiver over its own hotspot.

## Features

- **Local Hotspot Creation**: Automatically creates a local-only WiFi hotspot
- **AirPlay Service Discovery**: Advertises AirPlay service via mDNS (Bonjour)
- **Audio Playback**: Low-latency PCM audio playback using AudioTrack
- **Foreground Service**: Runs as a foreground service with notification
- **Simple UI**: Start/stop controls with status display

## Requirements

- Android 9.0 (API 28) or higher
- Permissions for location, WiFi control, and notifications
- NDK for native RAOP library (optional - stub included)

## Architecture

### Components

1. **MainActivity**: UI with start/stop buttons and status display
2. **ReceiverService**: Foreground service orchestrating all components
3. **HotspotController**: Manages LocalOnlyHotspot for WiFi AP
4. **MdnsAdvertiser**: Advertises AirPlay service using JmDNS
5. **AudioEngine**: Low-latency AudioTrack playback with jitter buffer
6. **RaopBridge**: JNI bridge to native RAOP protocol library (stub)

### Native Integration

The app includes **stub implementations** for the RAOP protocol. To enable actual AirPlay streaming:

1. Integrate a RAOP library (e.g., Shairport-sync) in `app/src/main/cpp/`
2. Update `CMakeLists.txt` with library configuration
3. Implement native methods in `raop_bridge.cpp`
4. Handle audio callbacks to feed PCM data to AudioEngine

See `app/src/main/cpp/README.md` for detailed integration instructions.

## Building

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK with API 28+
- Android NDK (for native builds)

### Build Steps

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build > Make Project
5. Run on device or emulator

### Command Line Build

```bash
./gradlew assembleDebug
```

## Usage

1. Launch the app
2. Grant required permissions (location, WiFi, notifications)
3. Tap "Start Speaker Mode"
4. Note the hotspot SSID and password displayed
5. Connect your iOS device to the hotspot
6. The device should appear in AirPlay menu
7. Stream audio to the device (requires native RAOP library)
8. Tap "Stop Speaker Mode" when done

## Current Status

- ✅ App builds successfully
- ✅ Foreground service starts/stops
- ✅ Hotspot creation works
- ✅ mDNS advertisement works
- ✅ Audio engine ready for playback
- ⚠️ Native RAOP library is **stub only** - no actual audio streaming
- ⚠️ Requires integration of real RAOP library for functionality

## Permissions

- `ACCESS_FINE_LOCATION`: Required for LocalOnlyHotspot
- `ACCESS_WIFI_STATE`: Read WiFi state
- `CHANGE_WIFI_STATE`: Control WiFi and hotspot
- `INTERNET`: Network communication
- `FOREGROUND_SERVICE`: Run foreground service
- `POST_NOTIFICATIONS`: Show notification (Android 13+)

## License

This is a skeleton implementation for educational purposes.

## Contributing

To complete the implementation:
1. Integrate a RAOP server library
2. Implement audio encryption/decryption
3. Add support for metadata and album art
4. Implement volume control
5. Add authentication support
