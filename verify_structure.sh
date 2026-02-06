#!/bin/bash

# Verification script for Music Streamer AirPlay Receiver skeleton

echo "=================================================="
echo "Music Streamer App - Structure Verification"
echo "=================================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        return 0
    else
        echo -e "${RED}✗${NC} $1 (MISSING)"
        return 1
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        return 0
    else
        echo -e "${RED}✗${NC} $1/ (MISSING)"
        return 1
    fi
}

PASS=0
FAIL=0

echo "1. Project Configuration Files"
echo "-------------------------------"
check_file "build.gradle" && ((PASS++)) || ((FAIL++))
check_file "settings.gradle" && ((PASS++)) || ((FAIL++))
check_file "gradle.properties" && ((PASS++)) || ((FAIL++))
check_file "gradlew" && ((PASS++)) || ((FAIL++))
check_file "app/build.gradle" && ((PASS++)) || ((FAIL++))
echo ""

echo "2. Android Manifest"
echo "-------------------"
check_file "app/src/main/AndroidManifest.xml" && ((PASS++)) || ((FAIL++))
echo ""

echo "3. Java Source Files"
echo "--------------------"
check_file "app/src/main/java/com/manoj077/musicstreamerapp/MainActivity.java" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/java/com/manoj077/musicstreamerapp/ReceiverService.java" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/java/com/manoj077/musicstreamerapp/HotspotController.java" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/java/com/manoj077/musicstreamerapp/MdnsAdvertiser.java" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/java/com/manoj077/musicstreamerapp/AudioEngine.java" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/java/com/manoj077/musicstreamerapp/RaopBridge.java" && ((PASS++)) || ((FAIL++))
echo ""

echo "4. Native C++ Files"
echo "-------------------"
check_file "app/src/main/cpp/CMakeLists.txt" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/cpp/raop_bridge.cpp" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/cpp/README.md" && ((PASS++)) || ((FAIL++))
echo ""

echo "5. Resource Files"
echo "-----------------"
check_file "app/src/main/res/layout/activity_main.xml" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/res/values/strings.xml" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/res/values/themes.xml" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/res/values/colors.xml" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/res/drawable/ic_launcher_foreground.xml" && ((PASS++)) || ((FAIL++))
check_file "app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" && ((PASS++)) || ((FAIL++))
echo ""

echo "6. Documentation"
echo "----------------"
check_file "README.md" && ((PASS++)) || ((FAIL++))
check_file "BUILD_INSTRUCTIONS.md" && ((PASS++)) || ((FAIL++))
echo ""

echo "7. Code Analysis"
echo "----------------"

# Count Java classes
JAVA_COUNT=$(find app/src/main/java -name "*.java" -type f | wc -l)
echo -e "${GREEN}✓${NC} Found $JAVA_COUNT Java source files"
((PASS++))

# Check for required methods in MainActivity
if grep -q "startReceiverService" app/src/main/java/com/manoj077/musicstreamerapp/MainActivity.java; then
    echo -e "${GREEN}✓${NC} MainActivity has startReceiverService method"
    ((PASS++))
else
    echo -e "${RED}✗${NC} MainActivity missing startReceiverService method"
    ((FAIL++))
fi

# Check for required methods in ReceiverService
if grep -q "startSpeakerMode" app/src/main/java/com/manoj077/musicstreamerapp/ReceiverService.java; then
    echo -e "${GREEN}✓${NC} ReceiverService has startSpeakerMode method"
    ((PASS++))
else
    echo -e "${RED}✗${NC} ReceiverService missing startSpeakerMode method"
    ((FAIL++))
fi

# Check for JNI methods in RaopBridge
if grep -q "native long nativeStart" app/src/main/java/com/manoj077/musicstreamerapp/RaopBridge.java; then
    echo -e "${GREEN}✓${NC} RaopBridge has native method declarations"
    ((PASS++))
else
    echo -e "${RED}✗${NC} RaopBridge missing native method declarations"
    ((FAIL++))
fi

# Check for System.loadLibrary in RaopBridge
if grep -q 'System.loadLibrary("raop")' app/src/main/java/com/manoj077/musicstreamerapp/RaopBridge.java; then
    echo -e "${GREEN}✓${NC} RaopBridge loads native library 'raop'"
    ((PASS++))
else
    echo -e "${RED}✗${NC} RaopBridge doesn't load native library"
    ((FAIL++))
fi

echo ""

echo "8. Manifest Verification"
echo "-------------------------"

# Check permissions
if grep -q "android.permission.ACCESS_FINE_LOCATION" app/src/main/AndroidManifest.xml; then
    echo -e "${GREEN}✓${NC} ACCESS_FINE_LOCATION permission declared"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Missing ACCESS_FINE_LOCATION permission"
    ((FAIL++))
fi

if grep -q "android.permission.FOREGROUND_SERVICE" app/src/main/AndroidManifest.xml; then
    echo -e "${GREEN}✓${NC} FOREGROUND_SERVICE permission declared"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Missing FOREGROUND_SERVICE permission"
    ((FAIL++))
fi

# Check service declaration
if grep -q "ReceiverService" app/src/main/AndroidManifest.xml; then
    echo -e "${GREEN}✓${NC} ReceiverService declared in manifest"
    ((PASS++))
else
    echo -e "${RED}✗${NC} ReceiverService not declared in manifest"
    ((FAIL++))
fi

if grep -q 'foregroundServiceType="mediaPlayback"' app/src/main/AndroidManifest.xml; then
    echo -e "${GREEN}✓${NC} Foreground service type set to mediaPlayback"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Missing foreground service type"
    ((FAIL++))
fi

echo ""

echo "9. Build Configuration"
echo "----------------------"

# Check minSdk
if grep -q "minSdk 28" app/build.gradle; then
    echo -e "${GREEN}✓${NC} minSdk set to 28 (Android 9.0)"
    ((PASS++))
else
    echo -e "${RED}✗${NC} minSdk not set to 28"
    ((FAIL++))
fi

# Check JmDNS dependency
if grep -q "jmdns" app/build.gradle; then
    echo -e "${GREEN}✓${NC} JmDNS dependency included"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Missing JmDNS dependency"
    ((FAIL++))
fi

# Check NDK configuration
if grep -q "externalNativeBuild" app/build.gradle; then
    echo -e "${GREEN}✓${NC} NDK/CMake configuration present"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Missing NDK/CMake configuration"
    ((FAIL++))
fi

echo ""
echo "=================================================="
echo "VERIFICATION SUMMARY"
echo "=================================================="
echo -e "${GREEN}Passed: $PASS${NC}"
echo -e "${RED}Failed: $FAIL${NC}"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}✓ All checks passed! Project structure is correct.${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Open project in Android Studio"
    echo "2. Sync Gradle files"
    echo "3. Build project"
    echo "4. See BUILD_INSTRUCTIONS.md for details"
    exit 0
else
    echo -e "${RED}✗ Some checks failed. Please review the issues above.${NC}"
    exit 1
fi
