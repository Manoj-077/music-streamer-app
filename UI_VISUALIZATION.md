# UI Screenshots (Visual Description)

Since this is a code skeleton without an actual build, here's what the UI will look like when the app runs:

## Main Screen (Stopped State)

```
┌────────────────────────────────────┐
│ ← Music Streamer          ☰       │
├────────────────────────────────────┤
│                                    │
│                                    │
│     AirPlay Speaker Mode          │
│                                    │
│                                    │
│       Status: Stopped             │
│                                    │
│                                    │
│                                    │
│ ┌────────────────────────────┐   │
│ │   Start Speaker Mode       │   │
│ └────────────────────────────┘   │
│                                    │
│ ┌────────────────────────────┐   │
│ │   Stop Speaker Mode        │   │
│ │     (disabled/grayed)       │   │
│ └────────────────────────────┘   │
│                                    │
│                                    │
│                                    │
│                                    │
└────────────────────────────────────┘
```

## Main Screen (Running State)

```
┌────────────────────────────────────┐
│ ← Music Streamer          ☰       │
├────────────────────────────────────┤
│                                    │
│                                    │
│     AirPlay Speaker Mode          │
│                                    │
│                                    │
│       Status: Running             │
│                                    │
│                                    │
│   Hotspot: AndroidShare_1234      │
│                                    │
│   Password: abcd5678              │
│                                    │
│   AirPlay Name: SM-G960F          │
│                                    │
│                                    │
│ ┌────────────────────────────┐   │
│ │   Start Speaker Mode       │   │
│ │     (disabled/grayed)       │   │
│ └────────────────────────────┘   │
│                                    │
│ ┌────────────────────────────┐   │
│ │   Stop Speaker Mode        │   │
│ └────────────────────────────┘   │
│                                    │
│                                    │
└────────────────────────────────────┘
```

## Notification (When Running)

```
┌────────────────────────────────────┐
│ 🔔 AirPlay Receiver                │
│ AirPlay receiver running           │
│ Tap to open                        │
└────────────────────────────────────┘
```

## Permission Request Dialogs

### 1. Location Permission
```
┌────────────────────────────────────┐
│  Allow Music Streamer to access   │
│  this device's location?           │
│                                    │
│  This permission is required for   │
│  WiFi hotspot functionality.       │
│                                    │
│  ┌─────────────┐  ┌────────────┐ │
│  │   DENY      │  │   ALLOW    │ │
│  └─────────────┘  └────────────┘ │
└────────────────────────────────────┘
```

### 2. Notification Permission (Android 13+)
```
┌────────────────────────────────────┐
│  Allow Music Streamer to send     │
│  notifications?                    │
│                                    │
│  ┌─────────────┐  ┌────────────┐ │
│  │   DENY      │  │   ALLOW    │ │
│  └─────────────┘  └────────────┘ │
└────────────────────────────────────┘
```

## iOS AirPlay Menu (How it appears)

When an iOS device connects to the hotspot:

```
┌────────────────────────────────────┐
│         AirPlay & Bluetooth        │
├────────────────────────────────────┤
│                                    │
│  ○ iPhone Speaker                  │
│                                    │
│  ⦿ SM-G960F                       │
│     📱 Nearby                      │
│                                    │
│  ○ Bedroom                         │
│     🏠 Home                         │
│                                    │
└────────────────────────────────────┘
```

## Color Scheme

- **Primary Color:** Purple (#6200EE)
- **Primary Variant:** Dark Purple (#3700B3)
- **Secondary Color:** Teal (#03DAC5)
- **Background:** White/Dark (Material theme)
- **Text:** Black/White (Material theme)

## UI Features

### When Stopped
- Title displayed at top
- Status shows "Stopped"
- Start button is **enabled** (full color)
- Stop button is **disabled** (grayed out)
- Info fields (hotspot, password, name) are **hidden**

### When Starting
- Status changes to "Starting..."
- Both buttons disabled briefly
- Hotspot creation in progress

### When Running
- Status shows "Running"
- Start button becomes **disabled** (grayed out)
- Stop button becomes **enabled** (full color)
- Info fields become **visible** showing:
  - Hotspot SSID
  - Hotspot password
  - AirPlay device name
- Notification appears in status bar

### When Stopping
- Status briefly shows "Stopped"
- UI resets to stopped state
- Notification disappears

## Responsive Layout

The layout uses ConstraintLayout for proper positioning:
- Title centered at top
- Status centered below title
- Info fields stacked vertically, centered
- Buttons full-width with margins
- 16dp padding throughout
- Proper spacing between elements

## Accessibility

- All buttons have content descriptions
- Text contrast meets WCAG standards
- Touch targets are 48dp minimum
- Font sizes scale with system settings

## Material Design Compliance

- Material Components theme
- Standard elevation for buttons
- Ripple effects on touch
- Standard color palette
- Standard typography scale
