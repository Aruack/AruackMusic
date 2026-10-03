package com.aruack.music.core.source.remote.lastfm

import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.PlaybackType
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.MusicProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LastFmSearchResponse(
    val results: LastFmResults? = null
)

@Serializable
data class LastFmResults(
    @SerialName("trackmatches") val trackmatches: LastFmTrackMatches? = null
)

@Serializable
data class LastFmTrackMatches(
    val track: List<LastFmTrack> = emptyList()
)

@Serializable
data class LastFmTrack(
    val name: String = "",
    val artist: String = "",
    val url: String = "",
    val listeners: String? = null,
    val image: List<LastFmImage> = emptyList()
)

@Serializable
data class LastFmImage(
    @SerialName("#text") val url: String = "",
    val size: String = ""
)

class LastFmProvider(
    private val httpClient: HttpClient
) : MusicProvider {

    override val source: MusicSource = MusicSource.LASTFM
    override val isOfflineAvailable: Boolean = false

    private val baseUrl = "https://ws.audioscrobbler.com/2.0"
    private val apiKey = "2c6e6b8c80775836a04efd451b63ef49"

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response: LastFmSearchResponse = httpClient.get(baseUrl) {
                parameter("method", "track.search")
                parameter("track", query)
                parameter("api_key", apiKey)
                parameter("format", "json")
                parameter("limit", "20")
            }.body()

            response.results?.trackmatches?.track?.mapNotNull { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrending(): List<Song> = emptyList()

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        search(genre)
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        search(language)
    }

    private fun LastFmTrack.toSong(): Song? {
        if (name.isBlank()) return null
        val artUrl = image.lastOrNull { it.url.isNotBlank() }?.url
        val lastFmUrl = url.ifBlank { "https://www.last.fm/music/${artist.replace(" ", "+")}/_/${name.replace(" ", "+")}" }

        return Song(
            id = "lastfm_${name.hashCode()}_${artist.hashCode()}",
            title = name,
            artist = artist.ifBlank { "Last.fm Artist" },
            album = "Last.fm Discovery",
            albumArtUri = artUrl,
            durationMs = 0L,
            mediaUri = lastFmUrl,
            sourceType = MusicSource.LASTFM,
            source = MusicSource.LASTFM,
            playbackType = PlaybackType.INFO_ONLY,
            externalUrl = lastFmUrl,
            licenseInfo = LicenseInfo(
                licenseName = "Last.fm Metadata",
                licenseUrl = "https://www.last.fm/legal",
                isCreativeCommons = false,
                isPublicDomain = false
            )
        )
    }
}
