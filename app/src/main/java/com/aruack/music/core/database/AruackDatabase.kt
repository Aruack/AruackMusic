package com.aruack.music.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aruack.music.core.database.dao.FavoriteDao
import com.aruack.music.core.database.dao.HistoryDao
import com.aruack.music.core.database.dao.PlaylistDao
import com.aruack.music.core.database.dao.QueueDao
import com.aruack.music.core.database.dao.SongDao
import com.aruack.music.core.database.entity.FavoriteEntity
import com.aruack.music.core.database.entity.HistoryEntity
import com.aruack.music.core.database.entity.PlaylistEntity
import com.aruack.music.core.database.entity.PlaylistItemEntity
import com.aruack.music.core.database.entity.QueueEntity
import com.aruack.music.core.database.entity.SongEntity

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistItemEntity::class,
        FavoriteEntity::class,
        HistoryEntity::class,
        QueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AruackDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
    abstract fun queueDao(): QueueDao

    companion object {
        const val DATABASE_NAME = "aruack_music.db"
    }
}
