package com.aruack.music.ui.screens.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aruack.music.core.model.Playlist
import com.aruack.music.ui.components.SongListItem
import com.aruack.music.ui.theme.GradientPrimary
import com.aruack.music.ui.theme.TextMuted
import com.aruack.music.ui.theme.TextPrimary
import com.aruack.music.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

val INDIAN_LANGUAGES = listOf(
    "Hindi", "Punjabi", "Tamil", "Telugu", "Bengali", "Marathi",
    "Gujarati", "Kannada", "Malayalam", "Bhojpuri", "Odia",
    "Assamese", "Rajasthani", "Haryanvi", "Kashmiri", "Urdu"
)

val INDIAN_GENRES = listOf(
    "Bollywood", "Sufi", "Ghazal", "Bhajan", "Devotional",
    "Indian Classical", "Hindustani Classical", "Carnatic",
    "Indian Folk", "Indian Indie", "Fusion", "Regional"
)

@Composable
fun HomeScreen(
    onNavigateToPlayer: () -> Unit,
    onNavigateToPlaylist: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Aruack Music",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Your Music. Your Way.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Private",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Direct-to-Device AMOLED Hero Badge
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GradientPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Multi-Provider Architecture",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = TextPrimary
                        )
                        Text(
                            text = "Local files, YouTube streaming, Spotify discovery, Archive.org & MusicBrainz.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Indian Music Languages Section
        item {
            SectionHeader(
                title = "Indian Music & Languages",
                subtitle = "Explore music in 16 Indian languages"
            )
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(INDIAN_LANGUAGES) { lang ->
                    val isSelected = selectedLanguage == lang
                    CategoryPill(
                        label = lang,
                        isSelected = isSelected,
                        icon = Icons.Default.Language,
                        onClick = {
                            selectedGenre = null
                            selectedLanguage = if (isSelected) null else lang
                            if (!isSelected) {
                                viewModel.selectIndianLanguage(lang)
                            }
                        }
                    )
                }
            }
        }

        // Indian Genres Section
        item {
            SectionHeader(
                title = "Indian & Regional Genres",
                subtitle = "Classical, Sufi, Bollywood, Devotional & more"
            )
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(INDIAN_GENRES) { genre ->
                    val isSelected = selectedGenre == genre
                    CategoryPill(
                        label = genre,
                        isSelected = isSelected,
                        icon = Icons.Default.Album,
                        onClick = {
                            selectedLanguage = null
                            selectedGenre = if (isSelected) null else genre
                            if (!isSelected) {
                                viewModel.selectIndianGenre(genre)
                            }
                        }
                    )
                }
            }
        }

        // Indian Music / Category Results (if selected or default)
        if (uiState.indianDiscoverySongs.isNotEmpty() || uiState.isLoadingCategory) {
            item {
                SectionHeader(
                    title = uiState.selectedCategoryTitle,
                    subtitle = "Real results from legal sources"
                )
            }

            if (uiState.isLoadingCategory) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
            } else {
                itemsIndexed(uiState.indianDiscoverySongs.take(8)) { index, song ->
                    SongListItem(
                        song = song,
                        isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                        isCurrent = playbackState.currentSong?.id == song.id,
                        onClick = { viewModel.playQueue(uiState.indianDiscoverySongs, index) },
                        onFavoriteToggle = { viewModel.toggleFavorite(song) },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Playlists Horizontal Row
        if (playlists.isNotEmpty()) {
            item {
                SectionHeader(title = "Your Playlists")
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistQuickCard(
                            playlist = playlist,
                            onClick = { onNavigateToPlaylist(playlist.id) }
                        )
                    }
                }
            }
        }

        // Recently Played
        if (recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader(title = "Recently Played")
            }
            itemsIndexed(recentlyPlayed.take(5)) { index, song ->
                SongListItem(
                    song = song,
                    isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                    isCurrent = playbackState.currentSong?.id == song.id,
                    onClick = { viewModel.playQueue(recentlyPlayed, index) },
                    onFavoriteToggle = { viewModel.toggleFavorite(song) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Favorites
        if (favorites.isNotEmpty()) {
            item {
                SectionHeader(title = "Favorites")
            }
            itemsIndexed(favorites.take(5)) { index, song ->
                SongListItem(
                    song = song,
                    isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                    isCurrent = playbackState.currentSong?.id == song.id,
                    onClick = { viewModel.playQueue(favorites, index) },
                    onFavoriteToggle = { viewModel.toggleFavorite(song) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Online Trending / Discover (Legal CC / Public Domain)
        item {
            SectionHeader(
                title = "Online Trending & Discovery",
                subtitle = "YouTube & Internet Archive"
            )
        }

        if (uiState.isLoadingTrending) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }
        } else if (uiState.trendingSongs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Connect to Wi-Fi/Mobile data to discover Creative Commons & Archive tracks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            itemsIndexed(uiState.trendingSongs) { index, song ->
                SongListItem(
                    song = song,
                    isPlaying = playbackState.isPlaying && playbackState.currentSong?.id == song.id,
                    isCurrent = playbackState.currentSong?.id == song.id,
                    onClick = { viewModel.playQueue(uiState.trendingSongs, index) },
                    onFavoriteToggle = { viewModel.toggleFavorite(song) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) Color.White else TextPrimary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = contentColor
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun PlaylistQuickCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QueueMusic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = "${playlist.songCount} songs",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = TextSecondary
            )
        }
    }
}
