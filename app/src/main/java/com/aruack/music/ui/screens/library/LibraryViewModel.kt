package com.aruack.music.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aruack.music.core.model.Album
import com.aruack.music.core.model.Artist
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

enum class LibraryTab(val title: String) {
    SONGS("Songs"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    FOLDERS("Folders"),
    PLAYLISTS("Playlists"),
    FAVORITES("Favorites")
}

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.SONGS,
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val folders: List<String> = emptyList(),
    val selectedFolderSongs: List<Song> = emptyList(),
    val activeFolder: String? = null,
    val isLoading: Boolean = false
)

class LibraryViewModel(
    private val musicRepository: MusicRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    val playlists: StateFlow<List<Playlist>> = musicRepository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Song>> = musicRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadLocalLibrary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val songs = musicRepository.getLocalSongs()
                val albums = musicRepository.getLocalAlbums()
                val artists = musicRepository.getLocalArtists()
                val folders = musicRepository.getLocalFolders()

                _uiState.value = _uiState.value.copy(
                    songs = songs,
                    albums = albums,
                    artists = artists,
                    folders = folders,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab, activeFolder = null)
    }

    fun selectFolder(folderPath: String) {
        viewModelScope.launch {
            val songs = musicRepository.getSongsByFolder(folderPath)
            _uiState.value = _uiState.value.copy(
                activeFolder = folderPath,
                selectedFolderSongs = songs
            )
        }
    }

    fun clearActiveFolder() {
        _uiState.value = _uiState.value.copy(activeFolder = null)
    }

    fun playSong(song: Song) {
        playbackManager.playSong(song)
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        playbackManager.playQueue(songs, startIndex)
    }

    fun shuffleAll(songs: List<Song>) {
        if (songs.isNotEmpty()) {
            val shuffled = songs.shuffled()
            playbackManager.playQueue(shuffled, 0)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.toggleFavorite(song)
        }
    }

    fun createPlaylist(name: String, description: String = "") {
        viewModelScope.launch {
            musicRepository.createPlaylist(name, description)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            musicRepository.deletePlaylist(playlistId)
        }
    }
}
