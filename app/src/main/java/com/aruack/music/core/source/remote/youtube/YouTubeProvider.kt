package com.aruack.music.core.source.remote.youtube

import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.MusicProvider
import com.aruack.music.core.source.remote.saavn.SaavnMusicSource
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias YouTubeMetadataProvider = YouTubeProvider

class YouTubeProvider(
    private val httpClient: HttpClient
) : MusicProvider {

    override val source: MusicSource = MusicSource.YOUTUBE
    override val isOfflineAvailable: Boolean = false

    private val streamingEngine = SaavnMusicSource(httpClient)

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        streamingEngine.search(query).map { song ->
            song.copy(
                id = song.id.replace("saavn_", "yt_"),
                sourceType = MusicSource.YOUTUBE,
                source = MusicSource.YOUTUBE
            )
        }
    }

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        streamingEngine.getTrending().map { song ->
            song.copy(
                id = song.id.replace("saavn_", "yt_"),
                sourceType = MusicSource.YOUTUBE,
                source = MusicSource.YOUTUBE
            )
        }
    }

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        streamingEngine.getByGenre(genre).map { song ->
            song.copy(
                id = song.id.replace("saavn_", "yt_"),
                sourceType = MusicSource.YOUTUBE,
                source = MusicSource.YOUTUBE
            )
        }
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        streamingEngine.getByLanguage(language).map { song ->
            song.copy(
                id = song.id.replace("saavn_", "yt_"),
                sourceType = MusicSource.YOUTUBE,
                source = MusicSource.YOUTUBE
            )
        }
    }

    override suspend fun getAlbums(artistId: String): List<Album> = withContext(Dispatchers.IO) {
        streamingEngine.getAlbums(artistId)
    }

    override suspend fun getArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        streamingEngine.getArtists(query)
    }
}
