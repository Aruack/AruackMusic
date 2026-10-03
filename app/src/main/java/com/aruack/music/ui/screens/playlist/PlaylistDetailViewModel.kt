package com.aruack.music.ui.screens.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.Song
import com.aruack.music.core.playback.PlaybackManager
import com.aruack.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    val playlistId: Long,
    private val musicRepository: MusicRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val playlistSongs: StateFlow<List<Song>> = musicRepository.getSongsInPlaylist(playlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    fun playSong(song: Song) {
        playbackManager.playSong(song)
    }

    fun playQueue(songs: List<Song>, startIndex: Int) {
        playbackManager.playQueue(songs, startIndex)
    }

    fun shuffleAll(songs: List<Song>) {
        if (songs.isNotEmpty()) {
            playbackManager.playQueue(songs.shuffled(), 0)
        }
    }

    fun removeSong(songId: String) {
        viewModelScope.launch {
            musicRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(song)
        }
    }
}
