package com.aruack.music.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.Song
import com.aruack.music.core.playback.PlaybackManager
import com.aruack.music.core.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedSource: AudioSourceType? = null,
    val searchResults: List<Song> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

class SearchViewModel(
    private val musicRepository: MusicRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        triggerSearch(newQuery, _uiState.value.selectedSource)
    }

    fun onClearQuery() {
        _uiState.value = _uiState.value.copy(
            query = "",
            searchResults = emptyList(),
            hasSearched = false
        )
    }

    fun selectSource(sourceType: AudioSourceType?) {
        _uiState.value = _uiState.value.copy(selectedSource = sourceType)
        if (_uiState.value.query.isNotBlank()) {
            triggerSearch(_uiState.value.query, sourceType)
        }
    }

    private fun triggerSearch(query: String, source: AudioSourceType?) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // Debounce typing
            _uiState.value = _uiState.value.copy(isSearching = true, hasSearched = true)
            try {
                val results = musicRepository.searchSongs(query, source)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isSearching = false)
            }
        }
    }

    fun playSong(song: Song) {
        playbackManager.playSong(song)
    }

    fun playQueue(songs: List<Song>, startIndex: Int) {
        playbackManager.playQueue(songs, startIndex)
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(song)
        }
    }
}
