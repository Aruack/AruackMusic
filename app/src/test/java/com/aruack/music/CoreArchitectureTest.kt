package com.aruack.music

import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.MusicSource
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.PlaybackType
import com.aruack.music.core.model.RepeatMode
import com.aruack.music.core.model.Song
import com.aruack.music.core.model.UnifiedSong
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreArchitectureTest {

    @Test
    fun testSongFormattedDuration() {
        val song = Song(
            id = "test_1",
            title = "Test Track",
            artist = "Aruack Artist",
            mediaUri = "https://example.com/audio.mp3",
            durationMs = 215000L // 3 min 35 sec
        )

        assertEquals("3:35", song.formattedDuration)
    }

    @Test
    fun testPlaybackStateCalculations() {
        val song = Song(
            id = "test_2",
            title = "Another Track",
            artist = "Aruack Artist",
            mediaUri = "https://example.com/audio2.mp3",
            durationMs = 180000L
        )

        val state = PlaybackState(
            currentSong = song,
            isPlaying = true,
            currentPositionMs = 90000L,
            durationMs = 180000L,
            repeatMode = RepeatMode.ALL
        )

        assertEquals(0.5f, state.progress, 0.001f)
        assertEquals("1:30", state.formattedCurrentPosition)
        assertEquals("3:00", state.formattedDuration)
        assertTrue(state.isPlaying)
        assertEquals(RepeatMode.ALL, state.repeatMode)
    }

    @Test
    fun testMusicSourceDisplayNames() {
        assertEquals("Local Audio", MusicSource.LOCAL.displayName)
        assertEquals("YouTube", MusicSource.YOUTUBE.displayName)
        assertEquals("Spotify", MusicSource.SPOTIFY.displayName)
        assertEquals("MusicBrainz", MusicSource.MUSICBRAINZ.displayName)
        assertEquals("Last.fm", MusicSource.LASTFM.displayName)
        assertEquals("Archive.org", MusicSource.INTERNET_ARCHIVE.displayName)
    }

    @Test
    fun testPlaybackTypeRules() {
        assertTrue(PlaybackType.LOCAL.isDirectPlayable)
        assertTrue(PlaybackType.DIRECT_STREAM.isDirectPlayable)
        assertFalse(PlaybackType.EXTERNAL_APP.isDirectPlayable)
        assertFalse(PlaybackType.INFO_ONLY.isDirectPlayable)
    }

    @Test
    fun testUnifiedSongConversion() {
        val unified = UnifiedSong(
            id = "yt_123",
            title = "Indian Sunrise",
            artist = "Aruack Artist",
            album = "Ragas",
            artworkUrl = "https://i.ytimg.com/vi/123/hqdefault.jpg",
            durationMs = 240000L,
            source = MusicSource.YOUTUBE,
            playbackType = PlaybackType.DIRECT_STREAM,
            playbackUrl = "https://inv.nadeko.net/latest_version?id=123&itag=140",
            externalUrl = "https://www.youtube.com/watch?v=123",
            language = "Hindi",
            genres = listOf("Indian Classical", "Fusion")
        )

        val song = unified.toSong()
        assertEquals("yt_123", song.id)
        assertEquals("Indian Sunrise", song.title)
        assertEquals(MusicSource.YOUTUBE, song.source)
        assertEquals(PlaybackType.DIRECT_STREAM, song.playbackType)
        assertTrue(song.playbackType.isDirectPlayable)
        assertEquals("https://inv.nadeko.net/latest_version?id=123&itag=140", song.mediaUri)

        val backToUnified = song.toUnifiedSong()
        assertEquals(unified.id, backToUnified.id)
        assertEquals(unified.playbackUrl, backToUnified.playbackUrl)
    }

    @Test
    fun testYouTubeStreamingRule() {
        val ytSong = Song(
            id = "yt_12345",
            title = "Kesariya",
            artist = "Arijit Singh",
            mediaUri = "https://inv.nadeko.net/latest_version?id=12345&itag=140",
            sourceType = MusicSource.YOUTUBE,
            source = MusicSource.YOUTUBE,
            playbackType = PlaybackType.DIRECT_STREAM,
            externalUrl = "https://www.youtube.com/watch?v=12345"
        )

        assertTrue(ytSong.playbackType.isDirectPlayable)
        val unified = ytSong.toUnifiedSong()
        assertEquals("https://inv.nadeko.net/latest_version?id=12345&itag=140", unified.playbackUrl)
        assertEquals("https://www.youtube.com/watch?v=12345", unified.externalUrl)
    }

    @Test
    fun testSpotifyStreamingRule() {
        val spotifySong = Song(
            id = "spotify_search_1",
            title = "Tum Hi Ho",
            artist = "Arijit Singh",
            mediaUri = "https://open.spotify.com/search/Tum+Hi+Ho",
            sourceType = MusicSource.SPOTIFY,
            source = MusicSource.SPOTIFY,
            playbackType = PlaybackType.DIRECT_STREAM,
            externalUrl = "https://open.spotify.com/search/Tum+Hi+Ho"
        )

        assertTrue(spotifySong.playbackType.isDirectPlayable)
        assertEquals("https://open.spotify.com/search/Tum+Hi+Ho", spotifySong.externalUrl)
    }

    @Test
    fun testLicenseInfoCreativeCommons() {
        val ccLicense = LicenseInfo(
            licenseName = "Creative Commons",
            licenseUrl = "https://creativecommons.org/licenses/by/3.0/",
            isCreativeCommons = true,
            isPublicDomain = false
        )

        assertTrue(ccLicense.isCreativeCommons)
        assertFalse(ccLicense.isPublicDomain)
        assertEquals("Creative Commons", ccLicense.licenseName)
    }
}
