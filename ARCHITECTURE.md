# 📐 Architecture Specification — Aruack Music

This document specifies the technical design, architectural guarantees, data storage contracts, and provider abstractions of **Aruack Music**.

---

## 1. Zero-Server Guarantee

Aruack Music strictly enforces a serverless architecture:

```
[User's Android Device] ──── (Direct Requests Only) ───► [Legal Public APIs / CDN]
        │
        ├─► Local File System (MediaStore API)
        ├─► Room SQLite Database (aruack_music.db)
        └─► DataStore Preferences (aruack_user_preferences)
```

- **No Intermediate Relay**: No reverse proxy or backend server operated by Aruack.
- **No Cloud Database**: User data, history, favorites, and playlists are never synchronized to external servers.
- **No User Identity / Auth Service**: The application functions immediately upon install with no login or signup flow.

---

## 2. Component Architecture

The codebase follows Clean Architecture principles with Jetpack Compose:

```
com.aruack.music
├── AruackApplication.kt
├── core
│   ├── database             # Room Database, DAOs, Entities
│   │   ├── AruackDatabase.kt
│   │   ├── dao (SongDao, PlaylistDao, FavoriteDao, HistoryDao, QueueDao)
│   │   └── entity (SongEntity, PlaylistEntity, PlaylistItemEntity, FavoriteEntity, HistoryEntity, QueueEntity)
│   ├── datastore            # Preferences DataStore (Theme, Accent, Toggles)
│   │   └── UserPreferencesRepository.kt
│   ├── model                # Domain models (Song, Album, Artist, Playlist, PlaybackState, LicenseInfo)
│   │   ├── Song.kt
│   │   ├── Album.kt
│   │   ├── Artist.kt
│   │   ├── Playlist.kt
│   │   ├── PlaybackState.kt
│   │   └── LicenseInfo.kt
│   ├── playback             # Media3 & ExoPlayer Engine
│   │   ├── AudioPlayerController.kt
│   │   ├── AruackMediaService.kt
│   │   └── PlaybackManager.kt
│   ├── repository           # Unified MusicRepository Implementation
│   │   ├── MusicRepository.kt
│   │   └── MusicRepositoryImpl.kt
│   └── source               # Provider Abstraction & Implementations
│       ├── MusicSource.kt
│       ├── local
│       │   └── LocalMediaStoreSource.kt
│       └── remote
│           ├── archive (InternetArchiveMusicSource.kt, ArchiveModels.kt)
│           └── jamendo (JamendoMusicSource.kt, JamendoModels.kt)
├── di                       # Koin Dependency Injection
│   └── AppModule.kt
└── ui                       # Jetpack Compose UI Layer
    ├── MainActivity.kt
    ├── components           # Reusable UI components (MiniPlayer, SongListItem, AlbumCard, SearchBar, Waveform)
    ├── navigation           # Compose Navigation Graph & Routes
    ├── screens
    │   ├── home             # Discovery & Recent Plays
    │   ├── library          # Local Songs, Albums, Artists, Folders, Playlists, Favorites
    │   ├── player           # Now Playing Screen & Queue Sheet
    │   ├── playlist         # Playlist Detail Screen
    │   ├── search           # Unified Multi-Source Search
    │   └── settings         # App Preferences & Serverless Info
    └── theme                # Ultra-modern dark theme, colors, typography, shapes
```

---

## 3. Data Storage Specifications

### Room Database (`aruack_music.db`)
- **`songs`**: Cached track metadata, duration, source origin, license information.
- **`playlists`**: User-defined local playlists (id, name, description, createdAt).
- **`playlist_items`**: Mapping table with cascade delete linking playlists to songs with ordering.
- **`favorites`**: Song IDs favorited by the user.
- **`playback_history`**: Chronological playback log for recent plays.
- **`playback_queue`**: Persisted queue state for seamless resume.

### DataStore Preferences (`aruack_user_preferences`)
- `app_theme`: `DARK`, `AMOLED`, `SYSTEM`
- `accent_color`: `INDIGO`, `CYAN`, `EMERALD`, `ROSE`, `AMBER`, `PURPLE`
- `offline_mode_only`: Boolean toggle to prevent any network calls
- `jamendo_enabled`: Boolean toggle for Jamendo API
- `archive_enabled`: Boolean toggle for Internet Archive API
- `last_song_id` & `last_playback_pos`: Resume state

---

## 4. Media Playback Subsystem

- **Media3 ExoPlayer**: Direct streaming of local content URIs (`content://media/external/audio/media/...`) and remote HTTPS audio streams.
- **Foreground Service**: `AruackMediaService` (`androidx.media3.session.MediaSessionService`) handles continuous background playback, notification controls, lock screen metadata, and audio focus management.

---

## 5. Adding a New Music Provider

To add a new legal music source:
1. Ensure the provider legally permits direct client queries and audio playback under its terms.
2. Implement `MusicSource`:
   ```kotlin
   class NewMusicSource(private val httpClient: HttpClient) : MusicSource { ... }
   ```
3. Register the provider in `di/AppModule.kt` and inject into `MusicRepositoryImpl`.
