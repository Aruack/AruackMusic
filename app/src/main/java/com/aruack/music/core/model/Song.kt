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
    val sourceType: AudioSourceType = AudioSourceType.LOCAL,
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
}
