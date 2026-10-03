package com.aruack.music.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.Playlist
import com.aruack.music.core.model.Song
import com.aruack.music.core.playback.PlaybackManager
import com.aruack.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentSongs: List<Song> = emptyList(),
    val trendingSongs: List<Song> = emptyList(),
    val quickLocalSongs: List<Song> = emptyList(),
    val indianDiscoverySongs: List<Song> = emptyList(),
    val selectedCategoryTitle: String = "Indian Music Hub",
    val isLoadingTrending: Boolean = false,
    val isLoadingCategory: Boolean = false
)

class HomeViewModel(
    private val musicRepository: MusicRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    val playlists: StateFlow<List<Playlist>> = musicRepository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayed: StateFlow<List<Song>> = musicRepository.getRecentlyPlayed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Song>> = musicRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTrending = true)
            try {
                val local = musicRepository.getLocalSongs()
                val trending = musicRepository.getTrending()
                val indianInitial = musicRepository.getSongsByGenre("Indian")
                _uiState.value = _uiState.value.copy(
                    quickLocalSongs = local.take(10),
                    trendingSongs = trending,
                    indianDiscoverySongs = indianInitial,
                    isLoadingTrending = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoadingTrending = false)
            }
        }
    }

    fun selectIndianLanguage(language: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCategory = true, selectedCategoryTitle = "$language Music")
            try {
                val results = musicRepository.getSongsByLanguage(language)
                _uiState.value = _uiState.value.copy(
                    indianDiscoverySongs = results,
                    isLoadingCategory = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoadingCategory = false)
            }
        }
    }

    fun selectIndianGenre(genre: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCategory = true, selectedCategoryTitle = "$genre Music")
            try {
                val results = musicRepository.getSongsByGenre(genre)
                _uiState.value = _uiState.value.copy(
                    indianDiscoverySongs = results,
                    isLoadingCategory = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoadingCategory = false)
            }
        }
    }

    fun playSong(song: Song) {
        playbackManager.playSong(song)
    }

    fun playQueue(songs: List<Song>, startIndex: Int) {
        playbackManager.playQueue(songs, startIndex)
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun playNext() {
        playbackManager.playNext()
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(song)
        }
    }
}
