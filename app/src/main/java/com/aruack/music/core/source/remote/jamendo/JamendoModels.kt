package com.aruack.music.core.source.remote.jamendo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JamendoApiResponse<T>(
    val headers: JamendoHeaders? = null,
    val results: List<T> = emptyList()
)

@Serializable
data class JamendoHeaders(
    val status: String = "success",
    val code: Int = 0,
    @SerialName("error_message") val errorMessage: String? = null
)

@Serializable
data class JamendoTrack(
    val id: String,
    val name: String,
    val duration: Long = 0L,
    @SerialName("artist_id") val artistId: String = "",
    @SerialName("artist_name") val artistName: String = "",
    @SerialName("album_name") val albumName: String = "",
    @SerialName("album_id") val albumId: String = "",
    @SerialName("license_ccurl") val licenseCcUrl: String = "",
    val position: Int = 0,
    @SerialName("releasedate") val releaseDate: String = "",
    val album_image: String? = null,
    val image: String? = null,
    val audio: String? = null,
    val audiodownload: String? = null
)

@Serializable
data class JamendoAlbum(
    val id: String,
    val name: String,
    @SerialName("releasedate") val releaseDate: String = "",
    @SerialName("artist_id") val artistId: String = "",
    @SerialName("artist_name") val artistName: String = "",
    val image: String? = null,
    @SerialName("zip") val zip: String? = null
)

@Serializable
data class JamendoArtist(
    val id: String,
    val name: String,
    val website: String? = null,
    @SerialName("joindate") val joinDate: String = "",
    val image: String? = null
)
