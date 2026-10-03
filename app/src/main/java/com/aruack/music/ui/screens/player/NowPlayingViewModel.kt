package com.aruack.music.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.Song
import com.aruack.music.core.playback.PlaybackManager
import com.aruack.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NowPlayingViewModel(
    private val playbackManager: PlaybackManager,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun playNext() {
        playbackManager.playNext()
    }

    fun playPrevious() {
        playbackManager.playPrevious()
    }

    fun seekTo(positionMs: Long) {
        playbackManager.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playbackManager.toggleShuffle()
    }

    fun cycleRepeatMode() {
        playbackManager.cycleRepeatMode()
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(song)
        }
    }

    fun playQueueIndex(index: Int) {
        val currentQueue = playbackState.value.queue
        if (index in currentQueue.indices) {
            playbackManager.playQueue(currentQueue, index)
        }
    }
}
