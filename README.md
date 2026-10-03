# 🎵 Aruack Music

### Your Music. Your Way.

Aruack Music is a **100% Serverless, Legal, Ad-Free, Open-Source Music Player for Android**. It is designed as a direct-client application that communicates directly with on-device media storage and authorized legal music providers—without any middleman server, proxy, tracking, or cloud account requirement.

---

## 📱 Download

Download the latest signed APK directly from GitHub Releases:

> 📦 **Latest Release:** [Aruack Music v1.0.0](https://github.com/your-username/aruack-music/releases/tag/v1.0.0)  
> **Asset Name:** `Aruack-Music-v1.0.0.apk`  
> **Package ID:** `online.aruack.music`

---

## ✨ Features

- 🎧 **Local Audio Playback**: Instant offline indexing of device audio files (`MP3`, `FLAC`, `WAV`, `AAC`, `OGG`, `M4A`) via Android `MediaStore`.
- 📁 **Folder Browser**: Navigate your music collection directly by storage folder structure.
- 📜 **Local Playlists & Favorites**: Create, edit, and organize custom playlists on-device using a local SQLite database (**Room**).
- 🌐 **Direct Legal Discovery**:
  - **Jamendo Music**: Search and stream tens of thousands of Creative Commons licensed songs via Jamendo API v3.0.
  - **Internet Archive**: Stream public domain live concerts and audio archives.
- 🎨 **Ultra-Modern Jetpack Compose UI**:
  - Dark & AMOLED themes with dynamic accent color choices (Indigo, Cyan, Emerald, Rose, Amber, Purple).
  - Floating `MiniPlayer` with interactive progress bar and quick controls.
  - Full-screen `NowPlaying` screen with animated wave visualizer, scrubbable seekbar, and queue sheet.
- 🔄 **Continuous Background Playback**:
  - Powered by **Android Media3 (ExoPlayer)** and `MediaSessionService`.
  - Notification controls, lock-screen metadata, and Bluetooth / headphone media button support.
- 🔒 **Zero Tracking & Privacy**: No user accounts, logins, telemetry, ads, or remote server dependencies.

---

## 🏛️ Architecture

```text
                 ┌─────────────────────────────────────────┐
                 │              ARUACK MUSIC               │
                 │          Android App (Compose)          │
                 │         (online.aruack.music)           │
                 └────────────────────┬────────────────────┘
                                      │
              ┌───────────────────────┼───────────────────────┐
              │                       │                       │
              ▼                       ▼                       ▼
      Local MediaStore          Jamendo API             Archive.org
     (Device Storage)      (Creative Commons Audio)   (Public Domain)
              │                       │                       │
              ▼                       ▼                       ▼
         Room Database             Direct Stream           Direct Stream
    (Playlists/Favorites)             Client                  Client
              │                       │                       │
              └───────────────────────┼───────────────────────┘
                                      │
                                      ▼
                             Android Media3 / ExoPlayer
                                      │
                                      ▼
                                 Audio Output
```

---

## 🌐 Supported Music Sources & Attribution

1. **On-Device Storage (`MediaStore`)**:
   - Reads existing audio files stored on the user's Android device.
   - Requires `READ_MEDIA_AUDIO` / `READ_EXTERNAL_STORAGE` permission.
2. **Jamendo Music (`https://www.jamendo.com`)**:
   - Legal Creative Commons music discovery and streaming via Jamendo API v3.0.
   - Client ID configured via `JAMENDO_CLIENT_ID` in `local.properties` or build configuration.
   - Jamendo tracks respect individual Creative Commons licensing terms.
3. **Internet Archive (`https://archive.org`)**:
   - Public domain audio, historical recordings, and live concerts.

---

## 🛡️ Privacy & Security

- **No Aruack Backend**: There is no Node.js, Python, Firebase, AWS, or proxy server operated by Aruack.
- **Zero Data Collection**: Playlists, listening history, favorites, and user preferences remain on your device.
- **No Ads or Trackers**: Completely free of AdMob, analytics SDKs, and trackers.
- **No Unauthorized Downloading**: Does not rip copyrighted streams or bypass DRM.

---

## 🛠️ Build from Source

### Prerequisites
- **Android Studio** (2023.1.1+ recommended)
- **JDK 17** or **JDK 21**
- **Android SDK** (API 34)

### Steps
```bash
# 1. Clone repository
git clone https://github.com/your-username/aruack-music.git
cd aruack-music

# 2. Configure local.properties (optional client ID)
cp local.properties.example local.properties

# 3. Build signed Release APK
./gradlew assembleRelease

# 4. Built APK output location:
# app/build/outputs/apk/release/app-release.apk
```

---

## ⚠️ Known Limitations

- Online streaming requires an active internet connection (Wi-Fi or Mobile Data).
- Local playback requires granting storage/audio permission when prompted.

---

## 📄 License

Licensed under the [Apache License, Version 2.0](LICENSE).
