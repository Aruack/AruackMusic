package com.aruack.music.core.source.remote.archive

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

typealias InternetArchiveProvider = InternetArchiveMusicSource

class InternetArchiveMusicSource(
    private val httpClient: HttpClient
) : MusicProvider {

    override val source: MusicSource = MusicSource.INTERNET_ARCHIVE
    override val isOfflineAvailable: Boolean = false

    private val searchUrl = "https://archive.org/advancedsearch.php"

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val q = "mediatype:audio AND (collection:audio_music OR collection:etree OR collection:georgeblood OR collection:audio_bookspoetry) AND ($query)"
            val response: ArchiveSearchResponse = httpClient.get(searchUrl) {
                parameter("q", q)
                parameter("fl[]", "identifier,title,creator,date,description,mediatype,collection,year")
                parameter("sort[]", "downloads desc")
                parameter("rows", "25")
                parameter("output", "json")
            }.body()

            response.response?.docs?.mapNotNull { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        try {
            val q = "mediatype:audio AND (collection:audio_music OR collection:etree) AND downloads:[1000 TO *]"
            val response: ArchiveSearchResponse = httpClient.get(searchUrl) {
                parameter("q", q)
                parameter("fl[]", "identifier,title,creator,date,description,mediatype,collection,year")
                parameter("sort[]", "downloads desc")
                parameter("rows", "20")
                parameter("output", "json")
            }.body()

            response.response?.docs?.mapNotNull { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        search(genre)
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        search(language)
    }

    override suspend fun getAlbums(artistId: String): List<Album> = withContext(Dispatchers.IO) {
        try {
            val q = "mediatype:audio AND creator:\"$artistId\""
            val response: ArchiveSearchResponse = httpClient.get(searchUrl) {
                parameter("q", q)
                parameter("fl[]", "identifier,title,creator,year")
                parameter("rows", "15")
                parameter("output", "json")
            }.body()

            response.response?.docs?.map { doc ->
                Album(
                    id = "archive_album_${doc.identifier}",
                    title = doc.title ?: doc.identifier,
                    artist = doc.creator ?: "Internet Archive",
                    artworkUri = "https://archive.org/services/img/${doc.identifier}",
                    year = doc.year?.toIntOrNull(),
                    sourceType = MusicSource.INTERNET_ARCHIVE
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        val songs = search(query)
        songs.groupBy { it.artist }
            .map { (artistName, artistSongs) ->
                Artist(
                    id = "archive_artist_${artistName.hashCode()}",
                    name = artistName,
                    artworkUri = artistSongs.firstOrNull()?.albumArtUri,
                    songCount = artistSongs.size,
                    sourceType = MusicSource.INTERNET_ARCHIVE
                )
            }
    }

    private fun ArchiveDoc.toSong(): Song? {
        val id = identifier.ifBlank { return null }
        val songTitle = title?.ifBlank { id } ?: id
        val songArtist = creator?.ifBlank { "Internet Archive" } ?: "Internet Archive"
        val thumbUrl = "https://archive.org/services/img/$id"
        val audioUrl = "https://archive.org/download/$id"

        return Song(
            id = "archive_$id",
            title = songTitle,
            artist = songArtist,
            album = "Live & Public Domain Archive",
            albumId = "archive_album_$id",
            albumArtUri = thumbUrl,
            durationMs = 180000L,
            mediaUri = audioUrl,
            sourceType = MusicSource.INTERNET_ARCHIVE,
            source = MusicSource.INTERNET_ARCHIVE,
            playbackType = PlaybackType.DIRECT_STREAM,
            externalUrl = "https://archive.org/details/$id",
            year = year?.toIntOrNull(),
            licenseInfo = LicenseInfo(
                licenseName = "Public Domain / CC",
                licenseUrl = "https://archive.org/about/",
                isCreativeCommons = true,
                isPublicDomain = true
            )
        )
    }
}
