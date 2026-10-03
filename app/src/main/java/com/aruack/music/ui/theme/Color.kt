package com.aruack.music.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF0D0F14)
val DarkSurface = Color(0xFF161922)
val DarkSurfaceElevated = Color(0xFF1E222D)
val DarkSurfaceGlass = Color(0xD9161922)
val DarkBorderGlass = Color(0x33FFFFFF)

val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF9CA3AF)
val TextMuted = Color(0xFF6B7280)

val AccentIndigo = Color(0xFF6366F1)
val AccentCyan = Color(0xFF06B6D4)
val AccentEmerald = Color(0xFF10B981)
val AccentRose = Color(0xFFF43F5E)
val AccentAmber = Color(0xFFF59E0B)
val AccentPurple = Color(0xFF8B5CF6)

val SourceLocalColor = Color(0xFF10B981)
val SourceJamendoColor = Color(0xFFEC4899)
val SourceArchiveColor = Color(0xFFF59E0B)

val GradientPrimary = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF06B6D4))
)

val GradientGlass = Brush.verticalGradient(
    colors = listOf(
        Color(0x33FFFFFF),
        Color(0x0DFFFFFF)
    )
)
