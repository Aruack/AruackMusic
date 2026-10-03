package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String? = null,
    val artworkUri: String? = null,
    val songCount: Int = 0,
    val year: Int? = null,
    val sourceType: AudioSourceType = AudioSourceType.LOCAL
)
