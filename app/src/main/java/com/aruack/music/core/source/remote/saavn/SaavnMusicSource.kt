package com.aruack.music.core.source.remote.saavn

import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.PlaybackType
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.MusicProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder

class SaavnMusicSource(
    private val httpClient: HttpClient
) : MusicProvider {

    override val source: MusicSource = MusicSource.YOUTUBE
    override val isOfflineAvailable: Boolean = false

    private val baseUrl = "https://www.jiosaavn.com/api.php"

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$baseUrl?__call=search.getResults&_format=json&_marker=0&api_version=4&ctx=web6dot0&n=25&p=1&q=$encodedQuery"
            val responseText = httpClient.get(url).bodyAsText()
            parseSongs(responseText)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        search("Top Trending Indian Hits 2026")
    }

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        search("$genre Top Hits")
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        search("$language Superhits")
    }

    override suspend fun getAlbums(artistId: String): List<Album> = withContext(Dispatchers.IO) {
        val songs = search(artistId)
        songs.groupBy { it.album }.map { (albumName, albumSongs) ->
            val first = albumSongs.first()
            Album(
                id = "saavn_album_${albumName.hashCode()}",
                title = albumName,
                artist = first.artist,
                artistId = first.artistId,
                artworkUri = first.albumArtUri,
                songCount = albumSongs.size,
                year = first.year,
                sourceType = MusicSource.YOUTUBE
            )
        }
    }

    override suspend fun getArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        val songs = search(query)
        songs.groupBy { it.artist }.map { (artistName, artistSongs) ->
            val first = artistSongs.first()
            Artist(
                id = "saavn_artist_${artistName.hashCode()}",
                name = artistName,
                artworkUri = first.albumArtUri,
                songCount = artistSongs.size,
                albumCount = 1,
                sourceType = MusicSource.YOUTUBE
            )
        }
    }

    private fun parseSongs(jsonText: String): List<Song> {
        val songList = mutableListOf<Song>()
        try {
            val json = JSONObject(jsonText)
            val results = json.optJSONArray("results") ?: return emptyList()

            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                if (id.isBlank()) continue

                val rawTitle = item.optString("title", "Unknown Title")
                val cleanTitle = SaavnUrlDecryptor.cleanHtml(rawTitle)

                val moreInfo = item.optJSONObject("more_info")
                val artist = moreInfo?.optString("music", "")?.ifBlank { null }
                    ?: moreInfo?.optString("singers", "")?.ifBlank { null }
                    ?: item.optString("subtitle", "Online Music")
                val cleanArtist = SaavnUrlDecryptor.cleanHtml(artist)

                val album = item.optString("album", "Aruack Stream")
                val cleanAlbum = SaavnUrlDecryptor.cleanHtml(album)

                val rawDuration = moreInfo?.optString("duration", "200") ?: "200"
                val durationSec = rawDuration.toLongOrNull() ?: 200L
                val durationMs = durationSec * 1000L

                val rawImage = item.optString("image", "")
                val albumArtUri = if (rawImage.isNotBlank()) SaavnUrlDecryptor.upgradeArtworkUrl(rawImage) else null

                val encryptedMediaUrl = moreInfo?.optString("encrypted_media_url", "") ?: ""
                val mediaUrl = SaavnUrlDecryptor.decrypt(encryptedMediaUrl)

                if (mediaUrl.isBlank()) continue

                val permaUrl = item.optString("perma_url", "https://www.jiosaavn.com/song/$id")

                songList.add(
                    Song(
                        id = "saavn_$id",
                        title = cleanTitle,
                        artist = cleanArtist,
                        artistId = "saavn_artist_${cleanArtist.hashCode()}",
                        album = cleanAlbum,
                        albumId = "saavn_album_${cleanAlbum.hashCode()}",
                        albumArtUri = albumArtUri,
                        durationMs = durationMs,
                        mediaUri = mediaUrl,
                        sourceType = MusicSource.YOUTUBE,
                        source = MusicSource.YOUTUBE,
                        playbackType = PlaybackType.DIRECT_STREAM,
                        externalUrl = permaUrl,
                        licenseInfo = LicenseInfo(
                            licenseName = "Online Audio Stream",
                            licenseUrl = permaUrl,
                            isCreativeCommons = false,
                            isPublicDomain = false
                        )
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return songList
    }
}
