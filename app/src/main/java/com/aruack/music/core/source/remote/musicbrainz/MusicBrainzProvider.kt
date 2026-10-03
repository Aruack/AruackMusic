package com.aruack.music.core.source.remote.musicbrainz

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
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicBrainzSearchResponse(
    val created: String? = null,
    val count: Int? = null,
    val offset: Int? = null,
    val recordings: List<MbRecording> = emptyList()
)

@Serializable
data class MbRecording(
    val id: String = "",
    val title: String = "",
    val length: Long? = null,
    @SerialName("artist-credit") val artistCredit: List<MbArtistCredit> = emptyList(),
    val releases: List<MbRelease> = emptyList(),
    val tags: List<MbTag> = emptyList()
)

@Serializable
data class MbArtistCredit(
    val name: String = "",
    val artist: MbArtistInfo? = null
)

@Serializable
data class MbArtistInfo(
    val id: String = "",
    val name: String = "",
    @SerialName("sort-name") val sortName: String? = null
)

@Serializable
data class MbRelease(
    val id: String = "",
    val title: String = "",
    val date: String? = null
)

@Serializable
data class MbTag(
    val count: Int = 0,
    val name: String = ""
)

class MusicBrainzProvider(
    private val httpClient: HttpClient
) : MusicProvider {

    override val source: MusicSource = MusicSource.MUSICBRAINZ
    override val isOfflineAvailable: Boolean = false

    private val baseUrl = "https://musicbrainz.org/ws/2"
    private val userAgent = "AruackMusic/1.0.1 ( contact@aruack.online )"

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response: MusicBrainzSearchResponse = httpClient.get("$baseUrl/recording") {
                header("User-Agent", userAgent)
                parameter("query", query)
                parameter("limit", "20")
                parameter("fmt", "json")
            }.body()

            response.recordings.mapNotNull { it.toSong() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrending(): List<Song> = emptyList()

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        search("tag:$genre")
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        search("tag:$language")
    }

    private fun MbRecording.toSong(): Song? {
        if (id.isBlank() || title.isBlank()) return null
        val artistName = artistCredit.joinToString(", ") { it.name }.ifBlank { "MusicBrainz Artist" }
        val artistId = artistCredit.firstOrNull()?.artist?.id
        val releaseName = releases.firstOrNull()?.title ?: "MusicBrainz Release"
        val releaseId = releases.firstOrNull()?.id
        val duration = length ?: 0L
        val mbUrl = "https://musicbrainz.org/recording/$id"
        val tagNames = tags.map { it.name }

        return Song(
            id = "mb_$id",
            title = title,
            artist = artistName,
            artistId = artistId,
            album = releaseName,
            albumId = releaseId,
            albumArtUri = if (releaseId != null) "https://coverartarchive.org/release/$releaseId/front-250" else null,
            durationMs = duration,
            mediaUri = mbUrl,
            sourceType = MusicSource.MUSICBRAINZ,
            source = MusicSource.MUSICBRAINZ,
            playbackType = PlaybackType.INFO_ONLY,
            externalUrl = mbUrl,
            genres = tagNames,
            genre = tagNames.firstOrNull(),
            licenseInfo = LicenseInfo(
                licenseName = "Open Database License (ODbL)",
                licenseUrl = "https://musicbrainz.org/doc/About/Data_License",
                isCreativeCommons = true,
                isPublicDomain = false
            )
        )
    }
}
