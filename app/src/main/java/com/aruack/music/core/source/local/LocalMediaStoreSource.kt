package com.aruack.music.core.source.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.PlaybackType
import com.aruack.music.core.model.Song
import com.aruack.music.core.source.MusicProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

typealias LocalMusicProvider = LocalMediaStoreSource

class LocalMediaStoreSource(
    private val context: Context
) : MusicProvider {

    override val source: MusicSource = MusicSource.LOCAL
    override val isOfflineAvailable: Boolean = true

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        val allSongs = getAllSongs()
        if (query.isBlank()) return@withContext allSongs
        val lowerQuery = query.lowercase().trim()
        allSongs.filter { song ->
            song.title.lowercase().contains(lowerQuery) ||
                song.artist.lowercase().contains(lowerQuery) ||
                song.album.lowercase().contains(lowerQuery)
        }
    }

    override suspend fun getTrending(): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().take(20)
    }

    override suspend fun getByGenre(genre: String): List<Song> = withContext(Dispatchers.IO) {
        val lower = genre.lowercase().trim()
        getAllSongs().filter { it.genre?.lowercase()?.contains(lower) == true || it.genres.any { g -> g.lowercase().contains(lower) } }
    }

    override suspend fun getByLanguage(language: String): List<Song> = withContext(Dispatchers.IO) {
        val lower = language.lowercase().trim()
        getAllSongs().filter { it.language?.lowercase()?.contains(lower) == true || it.title.lowercase().contains(lower) }
    }

    override suspend fun getAlbums(artistId: String): List<Album> = withContext(Dispatchers.IO) {
        val songs = getAllSongs()
        val filtered = if (artistId.isNotBlank()) {
            songs.filter { it.artistId == artistId || it.artist.equals(artistId, ignoreCase = true) }
        } else {
            songs
        }

        filtered.groupBy { it.albumId ?: it.album }
            .map { (_, albumSongs) ->
                val first = albumSongs.first()
                Album(
                    id = first.albumId ?: first.album,
                    title = first.album,
                    artist = first.artist,
                    artistId = first.artistId,
                    artworkUri = first.albumArtUri,
                    songCount = albumSongs.size,
                    year = first.year,
                    sourceType = MusicSource.LOCAL
                )
            }
    }

    override suspend fun getArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        val songs = getAllSongs()
        val filtered = if (query.isNotBlank()) {
            val lower = query.lowercase().trim()
            songs.filter { it.artist.lowercase().contains(lower) }
        } else {
            songs
        }

        filtered.groupBy { it.artistId ?: it.artist }
            .map { (_, artistSongs) ->
                val first = artistSongs.first()
                val albumsCount = artistSongs.map { it.album }.distinct().size
                Artist(
                    id = first.artistId ?: first.artist,
                    name = first.artist,
                    artworkUri = first.albumArtUri,
                    songCount = artistSongs.size,
                    albumCount = albumsCount,
                    sourceType = MusicSource.LOCAL
                )
            }
    }

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        val songList = mutableListOf<Song>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.SIZE
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 5000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val artistIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val trackColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
                val yearColumn = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
                val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Unknown Title"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val artistId = if (artistIdColumn != -1) cursor.getString(artistIdColumn) else null
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val albumId = if (albumIdColumn != -1) cursor.getLong(albumIdColumn) else -1L
                    val duration = cursor.getLong(durationColumn)
                    val filePath = if (dataColumn != -1) cursor.getString(dataColumn) else null
                    val track = if (trackColumn != -1) cursor.getInt(trackColumn) else null
                    val year = if (yearColumn != -1) cursor.getInt(yearColumn) else null
                    val size = if (sizeColumn != -1) cursor.getLong(sizeColumn) else 0L

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val albumArtUri = if (albumId != -1L) {
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()
                    } else null

                    val folderPath = filePath?.let { File(it).parent }

                    songList.add(
                        Song(
                            id = "local_$id",
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            artistId = artistId,
                            album = if (album == "<unknown>") "Unknown Album" else album,
                            albumId = if (albumId != -1L) albumId.toString() else null,
                            albumArtUri = albumArtUri,
                            durationMs = duration,
                            mediaUri = contentUri,
                            sourceType = MusicSource.LOCAL,
                            source = MusicSource.LOCAL,
                            playbackType = PlaybackType.LOCAL,
                            folderPath = folderPath,
                            year = if (year != null && year > 0) year else null,
                            trackNumber = track,
                            fileSize = size,
                            licenseInfo = LicenseInfo(
                                licenseName = "Local Device File",
                                isCreativeCommons = false,
                                isPublicDomain = false
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        songList
    }

    suspend fun getSongsByFolder(folderPath: String): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { it.folderPath == folderPath }
    }

    suspend fun getAllFolders(): List<String> = withContext(Dispatchers.IO) {
        getAllSongs().mapNotNull { it.folderPath }.distinct().sorted()
    }
}
