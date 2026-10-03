package com.aruack.music.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aruack.music.core.model.Playlist
import com.aruack.music.ui.components.AlbumCard
import com.aruack.music.ui.components.AudioPermissionLayout
import com.aruack.music.ui.components.SongListItem
import com.aruack.music.ui.theme.TextMuted
import com.aruack.music.ui.theme.TextPrimary
import com.aruack.music.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
    onNavigateToPlaylist: (Long) -> Unit,
    viewModel: LibraryViewModel = koinViewModel()
) {
    AudioPermissionLayout(
        onPermissionGranted = { viewModel.loadLocalLibrary() }
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val playbackState by viewModel.playbackState.collectAsState()
        val playlists by viewModel.playlists.collectAsState()
        val favorites by viewModel.favorites.collectAsState()

        var showCreatePlaylistDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Your Library",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "${uiState.songs.size} local tracks found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                // Tabs: Songs, Albums, Artists, Folders, Playlists, Favorites
                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    edgePadding = 20.dp,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    LibraryTab.values().forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontSize = 14.sp,
                                    color = if (uiState.selectedTab == tab) MaterialTheme.colorScheme.primary else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Content for selected tab
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    when (uiState.selectedTab) {
                        LibraryTab.SONGS -> {
                            SongsTabContent(
                                songs = uiState.songs,
                                playbackState = playbackState,
                                onSongClick = { index -> viewModel.playQueue(uiState.songs, index) },
                                onFavoriteToggle = { song -> viewModel.toggleFavorite(song) },
                                onPlayAll = { viewModel.playQueue(uiState.songs, 0) },
                                onShuffle = { viewModel.shuffleAll(uiState.songs) }
                            )
                        }
                        LibraryTab.ALBUMS -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 140.dp),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.albums) { album ->
                                    AlbumCard(
                                        album = album,
                                        onClick = { /* Could filter by album */ }
                                    )
                                }
                            }
                        }
                        LibraryTab.ARTISTS -> {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 90.dp)
                            ) {
                                items(uiState.artists) { artist ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = artist.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = artist.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${artist.songCount} songs • ${artist.albumCount} albums",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        LibraryTab.FOLDERS -> {
                            if (uiState.activeFolder != null) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { viewModel.clearActiveFolder() }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Back",
                                                tint = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = uiState.activeFolder ?: "",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            maxLines = 1
                                        )
                                    }
                                    LazyColumn(contentPadding = PaddingValues(bottom = 90.dp)) {
                                        itemsIndexed(uiState.selectedFolderSongs) { index, song ->
                                            SongListItem(
                                                song = song,
                                                isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                                                isCurrent = playbackState.currentSong?.id == song.id,
                                                onClick = { viewModel.playQueue(uiState.selectedFolderSongs, index) },
                                                onFavoriteToggle = { viewModel.toggleFavorite(song) }
                                            )
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(contentPadding = PaddingValues(bottom = 90.dp)) {
                                    items(uiState.folders) { folderPath ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.selectFolder(folderPath) }
                                                .padding(horizontal = 20.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Folder,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    text = folderPath.substringAfterLast('/'),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = folderPath,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                                    color = TextSecondary,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        LibraryTab.PLAYLISTS -> {
                            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)) {
                                items(playlists) { playlist ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onNavigateToPlaylist(playlist.id) }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QueueMusic,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = playlist.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${playlist.songCount} tracks • Local Room Database",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        LibraryTab.FAVORITES -> {
                            if (favorites.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No favorites yet. Tap the heart icon on any song to save it here.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted
                                    )
                                }
                            } else {
                                LazyColumn(contentPadding = PaddingValues(bottom = 90.dp)) {
                                    itemsIndexed(favorites) { index, song ->
                                        SongListItem(
                                            song = song,
                                            isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                                            isCurrent = playbackState.currentSong?.id == song.id,
                                            onClick = { viewModel.playQueue(favorites, index) },
                                            onFavoriteToggle = { viewModel.toggleFavorite(song) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Action Button to create playlist
            if (uiState.selectedTab == LibraryTab.PLAYLISTS) {
                FloatingActionButton(
                    onClick = { showCreatePlaylistDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 80.dp, end = 20.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Playlist")
                }
            }

            // Create Playlist Dialog
            if (showCreatePlaylistDialog) {
                var playlistName by remember { mutableStateOf("") }
                var playlistDesc by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showCreatePlaylistDialog = false },
                    title = { Text("New Local Playlist", color = TextPrimary) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = playlistName,
                                onValueChange = { playlistName = it },
                                label = { Text("Playlist Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = playlistDesc,
                                onValueChange = { playlistDesc = it },
                                label = { Text("Description (optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (playlistName.isNotBlank()) {
                                    viewModel.createPlaylist(playlistName.trim(), playlistDesc.trim())
                                    showCreatePlaylistDialog = false
                                }
                            }
                        ) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreatePlaylistDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SongsTabContent(
    songs: List<com.aruack.music.core.model.Song>,
    playbackState: com.aruack.music.core.model.PlaybackState,
    onSongClick: (Int) -> Unit,
    onFavoriteToggle: (com.aruack.music.core.model.Song) -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        if (songs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onPlayAll,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play All")
                    }

                    Button(
                        onClick = onShuffle,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shuffle", color = TextPrimary)
                    }
                }
            }
        }

        itemsIndexed(songs) { index, song ->
            SongListItem(
                song = song,
                isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                isCurrent = playbackState.currentSong?.id == song.id,
                onClick = { onSongClick(index) },
                onFavoriteToggle = { onFavoriteToggle(song) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
