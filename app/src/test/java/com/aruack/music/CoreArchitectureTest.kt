package com.aruack.music

import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.LicenseInfo
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.RepeatMode
import com.aruack.music.core.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun testAudioSourceTypeDisplayNames() {
        assertEquals("On-Device", AudioSourceType.LOCAL.displayName)
        assertEquals("Jamendo (CC)", AudioSourceType.JAMENDO.displayName)
        assertEquals("Internet Archive", AudioSourceType.INTERNET_ARCHIVE.displayName)
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
