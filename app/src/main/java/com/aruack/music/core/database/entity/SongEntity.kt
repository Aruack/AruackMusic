package com.aruack.music.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.LicenseInfo
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
            sourceType = try { AudioSourceType.valueOf(sourceType) } catch (e: Exception) { AudioSourceType.LOCAL },
            isFavorite = isFavorite,
            folderPath = folderPath,
            genre = genre,
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
                mediaUri = song.mediaUri,
                sourceType = song.sourceType.name,
                isFavorite = song.isFavorite,
                folderPath = song.folderPath,
                genre = song.genre,
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
