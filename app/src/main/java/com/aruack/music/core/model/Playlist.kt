package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val songCount: Int = 0,
    val coverArtUri: String? = null
)
