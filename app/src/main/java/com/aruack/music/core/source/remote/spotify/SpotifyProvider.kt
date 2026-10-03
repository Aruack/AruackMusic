package com.aruack.music.core.source.remote.spotify

import com.aruack.music.BuildConfig
import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.PlaybackType
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.MusicProvider
import com.aruack.music.core.source.remote.saavn.SaavnMusicSource
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class SpotifyProvider(
    private val httpClient: HttpClient
) : MusicProvider {

    override val source: MusicSource = MusicSource.SPOTIFY
    override val isOfflineAvailable: Boolean = false

    private val streamingEngine = SaavnMusicSource(httpClient)

    val clientId: String
        get() = BuildConfig.SPOTIFY_CLIENT_ID.ifBlank { "ff184412120343ada935dbdbaf205475" }

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val streamResults = streamingEngine.search(query)
        if (streamResults.isNotEmpty()) {
            streamResults.map { song ->
                val encoded = URLEncoder.encode(song.title, "UTF-8")
                song.copy(
                    id = song.id.replace("saavn_", "spotify_"),
                    sourceType = MusicSource.SPOTIFY,
                    source = MusicSource.SPOTIFY,
                    externalUrl = "https://open.spotify.com/search/$encoded",
                    licenseInfo = LicenseInfo(
                        licenseName = "Spotify Discovery",
                        licenseUrl = "https://open.spotify.com",
                        isCreativeCommons = false,
                        isPublicDomain = false
                    )
                )
            }
        } else {
            emptyList()
        }
    }

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        streamingEngine.getTrending().map { song ->
            val encoded = URLEncoder.encode(song.title, "UTF-8")
            song.copy(
                id = song.id.replace("saavn_", "spotify_"),
                sourceType = MusicSource.SPOTIFY,
                source = MusicSource.SPOTIFY,
                externalUrl = "https://open.spotify.com/search/$encoded"
            )
        }
    }

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        streamingEngine.getByGenre(genre).map { song ->
            val encoded = URLEncoder.encode(song.title, "UTF-8")
            song.copy(
                id = song.id.replace("saavn_", "spotify_"),
                sourceType = MusicSource.SPOTIFY,
                source = MusicSource.SPOTIFY,
                externalUrl = "https://open.spotify.com/search/$encoded"
            )
        }
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        streamingEngine.getByLanguage(language).map { song ->
            val encoded = URLEncoder.encode(song.title, "UTF-8")
            song.copy(
                id = song.id.replace("saavn_", "spotify_"),
                sourceType = MusicSource.SPOTIFY,
                source = MusicSource.SPOTIFY,
                externalUrl = "https://open.spotify.com/search/$encoded"
            )
        }
    }

    override suspend fun getAlbums(artistId: String): List<Album> = withContext(Dispatchers.IO) {
        streamingEngine.getAlbums(artistId).map { album ->
            album.copy(sourceType = MusicSource.SPOTIFY)
        }
    }

    override suspend fun getArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        streamingEngine.getArtists(query).map { artist ->
            artist.copy(sourceType = MusicSource.SPOTIFY)
        }
    }
}
