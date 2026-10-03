package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class MusicSource {
    LOCAL,
    YOUTUBE,
    SPOTIFY,
    INTERNET_ARCHIVE,
    MUSICBRAINZ,
    LASTFM;

    val displayName: String
        get() = when (this) {
            LOCAL -> "Local Audio"
            YOUTUBE -> "YouTube"
            SPOTIFY -> "Spotify"
            INTERNET_ARCHIVE -> "Archive.org"
            MUSICBRAINZ -> "MusicBrainz"
            LASTFM -> "Last.fm"
        }
}
