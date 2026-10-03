package com.aruack.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aruack.music.core.datastore.AccentColor
import com.aruack.music.core.datastore.AppTheme
import com.aruack.music.ui.theme.TextMuted
import com.aruack.music.ui.theme.TextPrimary
import com.aruack.music.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val prefs by viewModel.userPreferences.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp)
    ) {
        // Header
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )
            Text(
                text = "Preferences, Privacy & Provider Configuration",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Architecture Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Clean & Secure Architecture",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Zero dynamic code execution or remote bytecode\n• All playlists, favorites & history stored on-device in Room\n• Stream directly from YouTube & Archive.org\n• Discover metadata with Spotify & MusicBrainz\n• 100% private with no tracking or data collection",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Network & Providers
        item {
            SectionTitle("Music Providers & Connectivity")
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.CloudOff,
                title = "Offline Mode Only",
                subtitle = "Only scan and play local MediaStore files. Disables all online queries.",
                isChecked = prefs.isOfflineModeOnly,
                onCheckedChange = { viewModel.setOfflineModeOnly(it) }
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.Public,
                title = "YouTube (Audio Streaming & Search)",
                subtitle = "Search and stream music audio directly via YouTube engine.",
                isChecked = prefs.isYouTubeEnabled,
                onCheckedChange = { viewModel.setYouTubeEnabled(it) },
                enabled = !prefs.isOfflineModeOnly
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.Public,
                title = "Internet Archive (Audio Streams)",
                subtitle = "Stream public domain audio collections & live archives.",
                isChecked = prefs.isArchiveEnabled,
                onCheckedChange = { viewModel.setArchiveEnabled(it) },
                enabled = !prefs.isOfflineModeOnly
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.Public,
                title = "Spotify (Discovery & Client ID)",
                subtitle = "Discover metadata & stream tracks with Spotify Client ID.",
                isChecked = prefs.isSpotifyEnabled,
                onCheckedChange = { viewModel.setSpotifyEnabled(it) },
                enabled = !prefs.isOfflineModeOnly
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.Public,
                title = "YouTube (Metadata & Official Links)",
                subtitle = "Discover public song metadata & open official YouTube link.",
                isChecked = prefs.isYouTubeEnabled,
                onCheckedChange = { viewModel.setYouTubeEnabled(it) },
                enabled = !prefs.isOfflineModeOnly
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.Public,
                title = "MusicBrainz (Open Metadata)",
                subtitle = "Look up release dates, artists, and open music database tags.",
                isChecked = prefs.isMusicBrainzEnabled,
                onCheckedChange = { viewModel.setMusicBrainzEnabled(it) },
                enabled = !prefs.isOfflineModeOnly
            )
        }

        item {
            SettingsToggleRow(
                icon = Icons.Default.Public,
                title = "Last.fm (Discovery & Tags)",
                subtitle = "Explore top tracks, artist tags, and similar artists.",
                isChecked = prefs.isLastFmEnabled,
                onCheckedChange = { viewModel.setLastFmEnabled(it) },
                enabled = !prefs.isOfflineModeOnly
            )
        }

        // Section: Appearance
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Theme & Accent")
        }

        // Theme selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppTheme.values().forEach { themeOption ->
                    val isSelected = prefs.theme == themeOption
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.setTheme(themeOption) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = themeOption.name.lowercase().capitalize(),
                            color = if (isSelected) Color.White else TextPrimary,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp)
                        )
                    }
                }
            }
        }

        // Accent colors
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AccentColor.values().forEach { accent ->
                    val isSelected = prefs.accentColor == accent
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(accent.hex))
                            .clickable { viewModel.setAccentColor(accent) }
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Storage & History
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Storage & History")
        }

        item {
            Button(
                onClick = { viewModel.clearHistory() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Playback History", color = TextPrimary)
            }
        }

        // Section: About & Brand
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("About Aruack Music")
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Aruack Music",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Version 1.0.1 (Production Release)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tagline: Your Music. Your Way.\nBrand: ARUACK\nPackage: online.aruack.music\n\nLegal Notice: Aruack Music only plays on-device media and authorized Creative Commons / Public Domain streams. YouTube & Spotify items are metadata and external discovery integrations.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = if (enabled) TextPrimary else TextMuted
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
