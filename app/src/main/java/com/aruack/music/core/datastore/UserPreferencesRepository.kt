package com.aruack.music.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aruack_user_preferences")

enum class AppTheme {
    DARK,
    AMOLED,
    SYSTEM
}

enum class AccentColor(val hex: Long) {
    INDIGO(0xFF6366F1),
    CYAN(0xFF06B6D4),
    EMERALD(0xFF10B981),
    ROSE(0xFFF43F5E),
    AMBER(0xFFF59E0B),
    PURPLE(0xFF8B5CF6)
}

data class UserPreferences(
    val theme: AppTheme = AppTheme.DARK,
    val accentColor: AccentColor = AccentColor.INDIGO,
    val isOfflineModeOnly: Boolean = false,
    val isJamendoEnabled: Boolean = true,
    val isArchiveEnabled: Boolean = true,
    val autoPlayOnHeadphones: Boolean = false,
    val resumeOnReopen: Boolean = true,
    val lastPlayedSongId: String? = null,
    val lastPlaybackPositionMs: Long = 0L
)

class UserPreferencesRepository(
    private val context: Context
) {
    private object PreferencesKeys {
        val THEME = stringPreferencesKey("app_theme")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val OFFLINE_MODE_ONLY = booleanPreferencesKey("offline_mode_only")
        val JAMENDO_ENABLED = booleanPreferencesKey("jamendo_enabled")
        val ARCHIVE_ENABLED = booleanPreferencesKey("archive_enabled")
        val AUTOPLAY_HEADPHONES = booleanPreferencesKey("autoplay_headphones")
        val RESUME_ON_REOPEN = booleanPreferencesKey("resume_on_reopen")
        val LAST_SONG_ID = stringPreferencesKey("last_song_id")
        val LAST_PLAYBACK_POS = intPreferencesKey("last_playback_pos")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.DARK.name
            val theme = try { AppTheme.valueOf(themeName) } catch (e: Exception) { AppTheme.DARK }

            val accentName = preferences[PreferencesKeys.ACCENT_COLOR] ?: AccentColor.INDIGO.name
            val accent = try { AccentColor.valueOf(accentName) } catch (e: Exception) { AccentColor.INDIGO }

            val offlineModeOnly = preferences[PreferencesKeys.OFFLINE_MODE_ONLY] ?: false
            val jamendoEnabled = preferences[PreferencesKeys.JAMENDO_ENABLED] ?: true
            val archiveEnabled = preferences[PreferencesKeys.ARCHIVE_ENABLED] ?: true
            val autoplayHeadphones = preferences[PreferencesKeys.AUTOPLAY_HEADPHONES] ?: false
            val resumeOnReopen = preferences[PreferencesKeys.RESUME_ON_REOPEN] ?: true
            val lastSongId = preferences[PreferencesKeys.LAST_SONG_ID]
            val lastPlaybackPos = preferences[PreferencesKeys.LAST_PLAYBACK_POS]?.toLong() ?: 0L

            UserPreferences(
                theme = theme,
                accentColor = accent,
                isOfflineModeOnly = offlineModeOnly,
                isJamendoEnabled = jamendoEnabled,
                isArchiveEnabled = archiveEnabled,
                autoPlayOnHeadphones = autoplayHeadphones,
                resumeOnReopen = resumeOnReopen,
                lastPlayedSongId = lastSongId,
                lastPlaybackPositionMs = lastPlaybackPos
            )
        }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setAccentColor(accentColor: AccentColor) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCENT_COLOR] = accentColor.name
        }
    }

    suspend fun setOfflineModeOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_MODE_ONLY] = enabled
        }
    }

    suspend fun setJamendoEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.JAMENDO_ENABLED] = enabled
        }
    }

    suspend fun setArchiveEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ARCHIVE_ENABLED] = enabled
        }
    }

    suspend fun saveLastPlayback(songId: String, positionMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SONG_ID] = songId
            preferences[PreferencesKeys.LAST_PLAYBACK_POS] = positionMs.toInt()
        }
    }
}
