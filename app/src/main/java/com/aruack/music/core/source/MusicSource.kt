package com.aruack.music.core.source

import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.Song

interface MusicSource {
    val sourceType: AudioSourceType
    val isOfflineAvailable: Boolean

    suspend fun search(query: String): List<Song>

    suspend fun getTrending(): List<Song>

    suspend fun getAlbums(artistId: String): List<Album>

    suspend fun getArtists(query: String): List<Artist>
}
