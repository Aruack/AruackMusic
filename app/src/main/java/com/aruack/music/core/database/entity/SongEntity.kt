package com.aruack.music.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.PlaybackType
import com.aruack.music.core.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val artistId: String? = null,
    val album: String,
    val albumId: String? = null,
    val albumArtUri: String? = null,
    val durationMs: Long,
    val mediaUri: String,
    val sourceType: String,
    val isFavorite: Boolean = false,
    val folderPath: String? = null,
    val genre: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val fileSize: Long = 0L,
    val licenseName: String = "",
    val licenseUrl: String? = null,
    val isCreativeCommons: Boolean = false,
    val isPublicDomain: Boolean = false
) {
    fun toDomain(): Song {
        val src = try { 
            MusicSource.valueOf(sourceType) 
        } catch (e: Exception) { 
            MusicSource.LOCAL 
        }
        val playbackType = when (src) {
            MusicSource.LOCAL -> PlaybackType.LOCAL
            MusicSource.YOUTUBE, MusicSource.SPOTIFY, MusicSource.INTERNET_ARCHIVE -> PlaybackType.DIRECT_STREAM
            MusicSource.MUSICBRAINZ, MusicSource.LASTFM -> PlaybackType.INFO_ONLY
        }
        return Song(
            id = id,
            title = title,
            artist = artist,
            artistId = artistId,
            album = album,
            albumId = albumId,
            albumArtUri = albumArtUri,
            durationMs = durationMs,
            mediaUri = mediaUri,
            sourceType = src,
            source = src,
            playbackType = playbackType,
            externalUrl = if (!playbackType.isDirectPlayable) mediaUri else null,
            isFavorite = isFavorite,
            folderPath = folderPath,
            genre = genre,
            genres = if (genre.isNullOrBlank()) emptyList() else listOf(genre),
            year = year,
            trackNumber = trackNumber,
            fileSize = fileSize,
            licenseInfo = LicenseInfo(
                licenseName = licenseName,
                licenseUrl = licenseUrl,
                isCreativeCommons = isCreativeCommons,
                isPublicDomain = isPublicDomain
            )
        )
    }

    companion object {
        fun fromDomain(song: Song): SongEntity {
            return SongEntity(
                id = song.id,
                title = song.title,
                artist = song.artist,
                artistId = song.artistId,
                album = song.album,
                albumId = song.albumId,
                albumArtUri = song.albumArtUri,
                durationMs = song.durationMs,
                mediaUri = song.mediaUri.ifBlank { song.externalUrl ?: "" },
                sourceType = song.source.name,
                isFavorite = song.isFavorite,
                folderPath = song.folderPath,
                genre = song.genre ?: song.genres.firstOrNull(),
                year = song.year,
                trackNumber = song.trackNumber,
                fileSize = song.fileSize,
                licenseName = song.licenseInfo.licenseName,
                licenseUrl = song.licenseInfo.licenseUrl,
                isCreativeCommons = song.licenseInfo.isCreativeCommons,
                isPublicDomain = song.licenseInfo.isPublicDomain
            )
        }
    }
}
