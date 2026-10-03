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
import com.aruack.music.core.model.AudioSourceType
import com.aruack.music.ui.theme.SourceArchiveColor
import com.aruack.music.ui.theme.SourceJamendoColor
import com.aruack.music.ui.theme.SourceLocalColor

@Composable
fun SourceBadge(
    sourceType: AudioSourceType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (sourceType) {
        AudioSourceType.LOCAL -> Triple(
            SourceLocalColor.copy(alpha = 0.15f),
            SourceLocalColor,
            "Local"
        )
        AudioSourceType.JAMENDO -> Triple(
            SourceJamendoColor.copy(alpha = 0.15f),
            SourceJamendoColor,
            "Jamendo CC"
        )
        AudioSourceType.INTERNET_ARCHIVE -> Triple(
            SourceArchiveColor.copy(alpha = 0.15f),
            SourceArchiveColor,
            "Archive.org"
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
