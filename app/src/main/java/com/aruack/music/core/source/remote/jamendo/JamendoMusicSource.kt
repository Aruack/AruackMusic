package com.aruack.music.core.source.remote.jamendo

import com.aruack.music.BuildConfig
import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.MusicSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JamendoMusicSource(
    private val httpClient: HttpClient
) : MusicSource {

    override val sourceType: AudioSourceType = AudioSourceType.JAMENDO
    override val isOfflineAvailable: Boolean = false

    // Jamendo provides legal Creative Commons streaming.
    // Developers can supply their client_id in local.properties.
    // If none provided, a fallback demo key is used if available or search returns safely.
    private val clientId: String
        get() = BuildConfig.JAMENDO_CLIENT_ID.ifBlank { "84ce12ad" }

    private val baseUrl = "https://api.jamendo.com/v3.0"

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response: JamendoApiResponse<JamendoTrack> = httpClient.get("$baseUrl/tracks") {
                parameter("client_id", clientId)
                parameter("format", "json")
                parameter("limit", "30")
                parameter("search", query)
                parameter("include", "musicinfo")
                parameter("audioformat", "mp32")
            }.body()

            response.results.mapNotNull { it.toSong() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        try {
            val response: JamendoApiResponse<JamendoTrack> = httpClient.get("$baseUrl/tracks") {
                parameter("client_id", clientId)
                parameter("format", "json")
                parameter("limit", "25")
                parameter("order", "popularity_total")
                parameter("include", "musicinfo")
                parameter("audioformat", "mp32")
            }.body()

            response.results.mapNotNull { it.toSong() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getAlbums(artistId: String): List<Album> = withContext(Dispatchers.IO) {
        try {
            val response: JamendoApiResponse<JamendoAlbum> = httpClient.get("$baseUrl/albums") {
                parameter("client_id", clientId)
                parameter("format", "json")
                parameter("limit", "20")
                if (artistId.isNotBlank()) {
                    parameter("artist_id", artistId)
                }
            }.body()

            response.results.map { jamendoAlbum ->
                Album(
                    id = "jamendo_album_${jamendoAlbum.id}",
                    title = jamendoAlbum.name,
                    artist = jamendoAlbum.artistName,
                    artistId = "jamendo_artist_${jamendoAlbum.artistId}",
                    artworkUri = jamendoAlbum.image,
                    sourceType = AudioSourceType.JAMENDO
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        try {
            val response: JamendoApiResponse<JamendoArtist> = httpClient.get("$baseUrl/artists") {
                parameter("client_id", clientId)
                parameter("format", "json")
                parameter("limit", "20")
                if (query.isNotBlank()) {
                    parameter("namesearch", query)
                } else {
                    parameter("order", "popularity_total")
                }
            }.body()

            response.results.map { jamendoArtist ->
                Artist(
                    id = "jamendo_artist_${jamendoArtist.id}",
                    name = jamendoArtist.name,
                    artworkUri = jamendoArtist.image,
                    sourceType = AudioSourceType.JAMENDO
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun JamendoTrack.toSong(): Song? {
        val streamUrl = audio ?: return null
        return Song(
            id = "jamendo_$id",
            title = name,
            artist = artistName.ifBlank { "Jamendo Artist" },
            artistId = "jamendo_artist_$artistId",
            album = albumName.ifBlank { "Jamendo Single" },
            albumId = "jamendo_album_$albumId",
            albumArtUri = image ?: album_image,
            durationMs = duration * 1000L,
            mediaUri = streamUrl,
            sourceType = AudioSourceType.JAMENDO,
            trackNumber = position,
            licenseInfo = LicenseInfo(
                licenseName = "Creative Commons",
                licenseUrl = licenseCcUrl,
                isCreativeCommons = true,
                isPublicDomain = false,
                attributionRequired = true
            )
        )
    }
}
