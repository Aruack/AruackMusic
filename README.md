# 🎵 Aruack Music

### *Your Music. Your Way.*

**Brand:** ARUACK  
**Application Name:** Aruack Music  
**Package:** `online.aruack.music`  
**Platform:** Android (Target SDK 34, Min SDK 26)  
**License:** Apache License 2.0  

Aruack Music is a **modern, privacy-first, serverless music player and discovery application** for Android. Built with Jetpack Compose, Material 3, and Android Media3 (ExoPlayer), it delivers high-performance audio playback from on-device media and authorized legal streams—without any middleman server, cloud accounts, or tracking.

---

## 🚀 Features

- 🎧 **Local MediaStore Playback**: Instant offline indexing of device audio files (`MP3`, `FLAC`, `WAV`, `AAC`, `OGG`, `M4A`) with album art and folder navigation.
- 🇮🇳 **Indian Music Discovery Hub**: Dedicated discovery categories for **16 Indian languages** (*Hindi, Punjabi, Tamil, Telugu, Bengali, Marathi, Gujarati, Kannada, Malayalam, Bhojpuri, Odia, Assamese, Rajasthani, Haryanvi, Kashmiri, Urdu*) and **regional genres** (*Bollywood, Sufi, Ghazal, Bhajan, Devotional, Indian Classical, Carnatic, Hindustani Classical, Indian Folk, Fusion*).
- 🌐 **Clean Multi-Provider Architecture**:
  - **Local MediaStore**: Direct in-app playback with full ExoPlayer engine.
  - **YouTube Music**: Direct online streaming and search across millions of tracks and videos.
  - **Spotify**: Rich search and metadata discovery powered by Spotify Client ID (`ff184412120343ada935dbdbaf205475`).
  - **Internet Archive**: Stream legal public domain audio, concerts, and historical recordings.
  - **MusicBrainz**: Open metadata database for release information, tags, and recording details.
  - **Last.fm**: Track metadata, artist tags, and similar music discovery.
- 🎨 **Ultra-Modern AMOLED Compose UI**:
  - Pure AMOLED dark theme with custom accent colors (Indigo, Cyan, Emerald, Rose, Amber, Purple).
  - Multi-provider search filter chips (All, YouTube, Spotify, Local, Archive, MusicBrainz, Last.fm).
  - Floating `MiniPlayer` with waveform visualization and real-time playback controls.
  - Full-screen `NowPlaying` screen with seekbar, queue management, repeat, and shuffle.
- 🔄 **Continuous Background Playback**:
  - Built on **Android Media3 (ExoPlayer)** and `MediaSessionService`.
  - Notification controls, lock-screen metadata, headset controls, and audio focus management.
- 🔒 **Zero Tracking & 100% Privacy**: No user accounts, logins, telemetry, ads, or remote server dependencies.

---

## 🏛️ Provider Architecture & Playback Rules

```text
                               ┌─────────────────────────────────────────┐
                               │              ARUACK MUSIC               │
                               │          Android App (Compose)          │
                               │         (online.aruack.music)           │
                               └────────────────────┬────────────────────┘
                                                    │
             ┌──────────────────────┬───────────────┴──────────────┬──────────────────────┐
             │                      │                              │                      │
             ▼                      ▼                              ▼                      ▼
     Local MediaStore         YouTube Music                 Spotify Discovery       MusicBrainz / Last.fm
    (On-Device Audio)        (Audio Streams)               (Client ID ff1844...)       (Open Metadata)
             │                      │                              │                      │
             ▼                      ▼                              ▼                      ▼
       Playback: LOCAL        Playback: DIRECT_STREAM        Playback: DIRECT_STREAM Playback: INFO_ONLY
    (Android Media3 Player)(Android Media3 Player)        (Android Media3 Player) (Metadata & Details)
```

| Source | Playback Type | Behavior |
| :--- | :--- | :--- |
| **Local Device** | `LOCAL` | Direct Media3 / ExoPlayer in-app playback |
| **YouTube** | `DIRECT_STREAM` | Direct Media3 in-app playback of audio streams |
| **Spotify** | `DIRECT_STREAM` | Spotify Client ID metadata & streaming discovery |
| **Internet Archive** | `DIRECT_STREAM` | Direct Media3 in-app playback of public domain audio |
| **MusicBrainz** | `INFO_ONLY` | Metadata discovery; album art, release info, tags |
| **Last.fm** | `INFO_ONLY` | Metadata discovery; similar artists, tags, top tracks |

---

## 🛡️ Security Audit & Play Protect Compliance

Aruack Music strictly adheres to Google Play Protect and Android security standards:
- **Dedicated Release Keystore**: Signed with official RSA-2048 release certificate (V1, V2, and V3 signatures enabled).
- **Zero Dynamic Code Execution**: No `DexClassLoader`, `PathClassLoader`, `System.load`, or dynamic bytecode loading.
- **No Process Execution**: No `Runtime.exec` or `ProcessBuilder`.
- **No Unofficial YouTube Extraction**: Zero YouTube stream decryption, zero DRM bypass, zero yt-dlp binaries in the Android app.
- **Strict HTTPS Network Communication**: All network queries use secure TLS endpoints via Ktor Client with strict JSON serialization.
- **Minimal Permissions**: Requests only `READ_MEDIA_AUDIO`, `INTERNET`, and standard foreground playback service permissions.

---

## 🛠️ Building & Testing

### Prerequisites
- **JDK 21** or **JDK 17**
- **Android SDK** (API 34)

### Commands
```bash
# 1. Clone repository
git clone https://github.com/Aruack/AruackMusic.git
cd AruackMusic

# 2. Run unit tests
./gradlew test

# 3. Build signed production release APK
./gradlew assembleRelease

# Output APK:
# app/build/outputs/apk/release/app-release.apk
```

---

## 🐍 Standalone YouTube Metadata Service (Optional)

An optional, standalone Flask service is included in `youtube-service/` for searching YouTube metadata using `yt-dlp` in flat metadata mode. The Android application functions completely independently without this service.

```bash
cd youtube-service
pip install -r requirements.txt
python app.py
```

---

## 📄 License & Disclaimers

- **License**: [Apache License 2.0](LICENSE)
- **Third-Party Disclaimers**: Spotify, YouTube, Jamendo, MusicBrainz, Last.fm, and Internet Archive are trademarks of their respective owners. Aruack Music is an independent open-source application and does not claim ownership or rights to third-party content.
