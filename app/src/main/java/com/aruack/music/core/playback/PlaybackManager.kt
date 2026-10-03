package com.aruack.music.core.playback

import com.aruack.music.core.datastore.UserPreferencesRepository
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.Song
import com.aruack.music.core.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaybackManager(
    private val playerController: AudioPlayerController,
    private val musicRepository: MusicRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    init {
        // Observe song changes to record history and persist state locally
        scope.launch {
            playerController.playbackState.collect { state ->
                state.currentSong?.let { song ->
                    musicRepository.recordPlayedSong(song)
                    preferencesRepository.saveLastPlayback(song.id, state.currentPositionMs)
                }
            }
        }
    }

    fun playSong(song: Song) {
        playerController.playSong(song)
        scope.launch {
            musicRepository.saveQueue(listOf(song))
        }
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        playerController.playQueue(songs, startIndex)
        scope.launch {
            musicRepository.saveQueue(songs)
        }
    }

    fun togglePlayPause() {
        playerController.togglePlayPause()
    }

    fun playNext() {
        playerController.playNext()
    }

    fun playPrevious() {
        playerController.playPrevious()
    }

    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playerController.toggleShuffle()
    }

    fun cycleRepeatMode() {
        playerController.cycleRepeatMode()
    }
}
