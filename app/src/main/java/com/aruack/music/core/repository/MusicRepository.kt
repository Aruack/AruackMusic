package com.aruack.music.core.repository

import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.Playlist
import com.aruack.music.core.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    // Local Library
    suspend fun getLocalSongs(): List<Song>
    suspend fun getLocalAlbums(): List<Album>
    suspend fun getLocalArtists(): List<Artist>
    suspend fun getLocalFolders(): List<String>
    suspend fun getSongsByFolder(folderPath: String): List<Song>

    // Discovery (Online Legal Sources)
    suspend fun getTrending(): List<Song>
    suspend fun searchSongs(query: String, selectedSource: AudioSourceType? = null): List<Song>

    // Playlists
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String, description: String = ""): Long
    suspend fun deletePlaylist(playlistId: Long)
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    suspend fun addSongToPlaylist(playlistId: Long, song: Song)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)

    // Favorites
    fun getFavorites(): Flow<List<Song>>
    fun isFavorite(songId: String): Flow<Boolean>
    suspend fun toggleFavorite(song: Song)

    // History
    fun getRecentlyPlayed(): Flow<List<Song>>
    suspend fun recordPlayedSong(song: Song)
    suspend fun clearHistory()

    // Queue persistence
    suspend fun saveQueue(songs: List<Song>)
    suspend fun getSavedQueue(): List<Song>
}
