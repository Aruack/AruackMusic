package com.aruack.music.core.repository

import com.aruack.music.core.database.AruackDatabase
import com.aruack.music.core.database.entity.FavoriteEntity
import com.aruack.music.core.database.entity.HistoryEntity
import com.aruack.music.core.database.entity.PlaylistEntity
import com.aruack.music.core.database.entity.PlaylistItemEntity
import com.aruack.music.core.database.entity.SongEntity
import com.aruack.music.core.datastore.UserPreferencesRepository
import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.Playlist
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.local.LocalMediaStoreSource
import com.aruack.music.core.source.remote.archive.InternetArchiveMusicSource
import com.aruack.music.core.source.remote.jamendo.JamendoMusicSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicRepositoryImpl(
    private val localSource: LocalMediaStoreSource,
    private val jamendoSource: JamendoMusicSource,
    private val archiveSource: InternetArchiveMusicSource,
    private val database: AruackDatabase,
    private val preferencesRepository: UserPreferencesRepository
) : MusicRepository {

    override suspend fun getLocalSongs(): List<Song> = withContext(Dispatchers.IO) {
        val favoriteIds = database.favoriteDao().getAllFavoriteSongIds().toSet()
        val songs = localSource.getAllSongs().map { song ->
            if (favoriteIds.contains(song.id)) song.copy(isFavorite = true) else song
        }
        // Cache metadata locally in Room
        if (songs.isNotEmpty()) {
            database.songDao().insertSongs(songs.map { SongEntity.fromDomain(it) })
        }
        songs
    }

    override suspend fun getLocalAlbums(): List<Album> = localSource.getAlbums("")

    override suspend fun getLocalArtists(): List<Artist> = localSource.getArtists("")

    override suspend fun getLocalFolders(): List<String> = localSource.getAllFolders()

    override suspend fun getSongsByFolder(folderPath: String): List<Song> = localSource.getSongsByFolder(folderPath)

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        if (prefs.isOfflineModeOnly) {
            return@withContext getLocalSongs().take(20)
        }

        val results = mutableListOf<Song>()
        if (prefs.isJamendoEnabled) {
            results.addAll(jamendoSource.getTrending())
        }
        if (prefs.isArchiveEnabled) {
            results.addAll(archiveSource.getTrending())
        }
        if (results.isEmpty()) {
            results.addAll(getLocalSongs().take(20))
        }
        results
    }

    override suspend fun searchSongs(query: String, selectedSource: AudioSourceType?): List<Song> = withContext(Dispatchers.IO) {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        val results = mutableListOf<Song>()

        when (selectedSource) {
            AudioSourceType.LOCAL -> {
                results.addAll(localSource.search(query))
            }
            AudioSourceType.JAMENDO -> {
                if (!prefs.isOfflineModeOnly && prefs.isJamendoEnabled) {
                    results.addAll(jamendoSource.search(query))
                }
            }
            AudioSourceType.INTERNET_ARCHIVE -> {
                if (!prefs.isOfflineModeOnly && prefs.isArchiveEnabled) {
                    results.addAll(archiveSource.search(query))
                }
            }
            null -> {
                // Unified search across local + enabled remote legal sources
                results.addAll(localSource.search(query))
                if (!prefs.isOfflineModeOnly) {
                    if (prefs.isJamendoEnabled) {
                        results.addAll(jamendoSource.search(query))
                    }
                    if (prefs.isArchiveEnabled) {
                        results.addAll(archiveSource.search(query))
                    }
                }
            }
        }

        val favoriteIds = database.favoriteDao().getAllFavoriteSongIds().toSet()
        results.map { song ->
            if (favoriteIds.contains(song.id)) song.copy(isFavorite = true) else song
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return database.playlistDao().getAllPlaylistsFlow().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun createPlaylist(name: String, description: String): Long = withContext(Dispatchers.IO) {
        database.playlistDao().insertPlaylist(
            PlaylistEntity(name = name, description = description)
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        database.playlistDao().deletePlaylist(playlistId)
    }

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return database.playlistDao().getSongsInPlaylistFlow(playlistId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addSongToPlaylist(playlistId: Long, song: Song) = withContext(Dispatchers.IO) {
        database.songDao().insertSong(SongEntity.fromDomain(song))
        database.playlistDao().addSongToPlaylist(
            PlaylistItemEntity(
                playlistId = playlistId,
                songId = song.id,
                position = 0
            )
        )
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) = withContext(Dispatchers.IO) {
        database.playlistDao().removeSongFromPlaylist(playlistId, songId)
    }

    override fun getFavorites(): Flow<List<Song>> {
        return database.favoriteDao().getFavoriteSongsFlow().map { entities ->
            entities.map { it.toDomain().copy(isFavorite = true) }
        }
    }

    override fun isFavorite(songId: String): Flow<Boolean> {
        return database.favoriteDao().isFavoriteFlow(songId)
    }

    override suspend fun toggleFavorite(song: Song) = withContext(Dispatchers.IO) {
        database.songDao().insertSong(SongEntity.fromDomain(song))
        val isFav = database.favoriteDao().isFavorite(song.id)
        if (isFav) {
            database.favoriteDao().removeFavorite(song.id)
        } else {
            database.favoriteDao().addFavorite(FavoriteEntity(songId = song.id))
        }
    }

    override fun getRecentlyPlayed(): Flow<List<Song>> {
        return database.historyDao().getRecentlyPlayedFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun recordPlayedSong(song: Song) = withContext(Dispatchers.IO) {
        database.songDao().insertSong(SongEntity.fromDomain(song))
        database.historyDao().addHistory(
            HistoryEntity(
                songId = song.id,
                playedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        database.historyDao().clearHistory()
    }

    override suspend fun saveQueue(songs: List<Song>) = withContext(Dispatchers.IO) {
        database.songDao().insertSongs(songs.map { SongEntity.fromDomain(it) })
        database.queueDao().saveQueue(songs.map { SongEntity.fromDomain(it) })
    }

    override suspend fun getSavedQueue(): List<Song> = withContext(Dispatchers.IO) {
        database.queueDao().getSavedQueue().map { it.toDomain() }
    }
}
