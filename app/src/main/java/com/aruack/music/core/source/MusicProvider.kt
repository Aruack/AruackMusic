package com.aruack.music.core.source

import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.Song

interface MusicProvider {
    val source: MusicSource
    val isOfflineAvailable: Boolean get() = false

    suspend fun search(query: String): List<Song>
    suspend fun getTrending(): List<Song> = emptyList()
    suspend fun getAlbums(artistId: String): List<Album> = emptyList()
    suspend fun getArtists(query: String): List<Artist> = emptyList()
    suspend fun getByGenre(genre: String): List<Song> = emptyList()
    suspend fun getByLanguage(language: String): List<Song> = emptyList()
}
