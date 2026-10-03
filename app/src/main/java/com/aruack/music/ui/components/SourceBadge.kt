package com.aruack.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aruack.music.core.model.MusicSource
import com.aruack.music.ui.theme.SourceArchiveColor
import com.aruack.music.ui.theme.SourceLastFmColor
import com.aruack.music.ui.theme.SourceLocalColor
import com.aruack.music.ui.theme.SourceMusicBrainzColor
import com.aruack.music.ui.theme.SourceSpotifyColor
import com.aruack.music.ui.theme.SourceYouTubeColor

@Composable
fun SourceBadge(
    sourceType: MusicSource,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (sourceType) {
        MusicSource.LOCAL -> Triple(
            SourceLocalColor.copy(alpha = 0.15f),
            SourceLocalColor,
            "Local"
        )
        MusicSource.YOUTUBE -> Triple(
            SourceYouTubeColor.copy(alpha = 0.15f),
            SourceYouTubeColor,
            "YouTube"
        )
        MusicSource.SPOTIFY -> Triple(
            SourceSpotifyColor.copy(alpha = 0.15f),
            SourceSpotifyColor,
            "Spotify"
        )
        MusicSource.INTERNET_ARCHIVE -> Triple(
            SourceArchiveColor.copy(alpha = 0.15f),
            SourceArchiveColor,
            "Archive.org"
        )
        MusicSource.MUSICBRAINZ -> Triple(
            SourceMusicBrainzColor.copy(alpha = 0.15f),
            SourceMusicBrainzColor,
            "MusicBrainz"
        )
        MusicSource.LASTFM -> Triple(
            SourceLastFmColor.copy(alpha = 0.15f),
            SourceLastFmColor,
            "Last.fm"
        )
    }

    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            lineHeight = 12.sp
        )
    }
}
