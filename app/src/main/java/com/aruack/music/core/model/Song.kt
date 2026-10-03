package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String? = null,
    val album: String = "Unknown Album",
    val albumId: String? = null,
    val albumArtUri: String? = null,
    val durationMs: Long = 0L,
    val mediaUri: String,
    val sourceType: MusicSource = MusicSource.LOCAL,
    val source: MusicSource = sourceType,
    val playbackType: PlaybackType = if (sourceType == MusicSource.LOCAL) PlaybackType.LOCAL else if (sourceType == MusicSource.YOUTUBE || sourceType == MusicSource.SPOTIFY || sourceType == MusicSource.INTERNET_ARCHIVE) PlaybackType.DIRECT_STREAM else PlaybackType.INFO_ONLY,
    val externalUrl: String? = null,
    val language: String? = null,
    val genres: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val folderPath: String? = null,
    val genre: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val bitRate: Int? = null,
    val fileSize: Long = 0L,
    val licenseInfo: LicenseInfo = LicenseInfo()
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds % 60
            return "%d:%02d".format(minutes, remainingSeconds)
        }

    fun toUnifiedSong(): UnifiedSong {
        return UnifiedSong(
            id = id,
            title = title,
            artist = artist,
            album = album,
            artworkUrl = albumArtUri,
            durationMs = durationMs,
            source = source,
            playbackType = playbackType,
            playbackUrl = if (playbackType.isDirectPlayable) mediaUri else null,
            externalUrl = externalUrl ?: if (!playbackType.isDirectPlayable) mediaUri else null,
            language = language,
            genres = genres.ifEmpty { if (genre != null) listOf(genre) else emptyList() }
        )
    }
}
