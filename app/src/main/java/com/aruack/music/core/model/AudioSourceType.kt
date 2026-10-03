package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AudioSourceType {
    LOCAL,
    JAMENDO,
    INTERNET_ARCHIVE;

    val displayName: String
        get() = when (this) {
            LOCAL -> "On-Device"
            JAMENDO -> "Jamendo (CC)"
            INTERNET_ARCHIVE -> "Internet Archive"
        }
}
