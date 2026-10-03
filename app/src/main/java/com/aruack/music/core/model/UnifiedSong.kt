package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UnifiedSong(
    val id: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val artworkUrl: String?,
    val durationMs: Long?,
    val source: MusicSource,
    val playbackType: PlaybackType,
    val playbackUrl: String?,
    val externalUrl: String?,
    val language: String?,
    val genres: List<String> = emptyList()
) {
    val formattedDuration: String
        get() {
            val totalMs = durationMs ?: 0L
            val totalSeconds = totalMs / 1000
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds % 60
            return "%d:%02d".format(minutes, remainingSeconds)
        }

    fun toSong(): Song {
        return Song(
            id = id,
            title = title,
            artist = artist ?: "Unknown Artist",
            album = album ?: "Unknown Album",
            albumArtUri = artworkUrl,
            durationMs = durationMs ?: 0L,
            mediaUri = playbackUrl ?: externalUrl ?: "",
            sourceType = source,
            source = source,
            playbackType = playbackType,
            externalUrl = externalUrl,
            language = language,
            genres = genres
        )
    }
}
