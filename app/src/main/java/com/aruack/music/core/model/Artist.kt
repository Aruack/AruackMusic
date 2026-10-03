package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val artworkUri: String? = null,
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val sourceType: AudioSourceType = AudioSourceType.LOCAL
)
