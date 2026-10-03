package com.aruack.music.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aruack.music.core.database.entity.FavoriteEntity
import com.aruack.music.core.database.entity.HistoryEntity
import com.aruack.music.core.database.entity.PlaylistEntity
import com.aruack.music.core.database.entity.PlaylistItemEntity
import com.aruack.music.core.database.entity.QueueEntity
import com.aruack.music.core.database.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun removeFavorite(songId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    fun isFavoriteFlow(songId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavorite(songId: String): Boolean

    @Query("SELECT s.* FROM songs s INNER JOIN favorites f ON s.id = f.songId ORDER BY f.addedAt DESC")
    fun getFavoriteSongsFlow(): Flow<List<SongEntity>>

    @Query("SELECT songId FROM favorites")
    suspend fun getAllFavoriteSongIds(): List<String>
}

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :name, description = :description WHERE id = :playlistId")
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(item: PlaylistItemEntity)

    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)

    @Query("SELECT s.* FROM songs s INNER JOIN playlist_items pi ON s.id = pi.songId WHERE pi.playlistId = :playlistId ORDER BY pi.position ASC")
    fun getSongsInPlaylistFlow(playlistId: Long): Flow<List<SongEntity>>

    @Query("SELECT COUNT(*) FROM playlist_items WHERE playlistId = :playlistId")
    fun getPlaylistSongCountFlow(playlistId: Long): Flow<Int>
}

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistory(history: HistoryEntity)

    @Query("SELECT s.* FROM songs s INNER JOIN playback_history h ON s.id = h.songId ORDER BY h.playedAt DESC LIMIT :limit")
    fun getRecentlyPlayedFlow(limit: Int = 30): Flow<List<SongEntity>>

    @Query("DELETE FROM playback_history")
    suspend fun clearHistory()
}

@Dao
interface QueueDao {

    @Query("SELECT s.* FROM songs s INNER JOIN playback_queue q ON s.id = q.songId ORDER BY q.queueOrder ASC")
    suspend fun getSavedQueue(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItems(items: List<QueueEntity>)

    @Query("DELETE FROM playback_queue")
    suspend fun clearQueue()

    @Transaction
    suspend fun saveQueue(songs: List<SongEntity>) {
        clearQueue()
        val queueItems = songs.mapIndexed { index, song ->
            QueueEntity(queueOrder = index, songId = song.id)
        }
        insertQueueItems(queueItems)
    }
}
