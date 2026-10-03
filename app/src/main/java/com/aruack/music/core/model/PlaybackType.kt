package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class PlaybackType {
    LOCAL,
    DIRECT_STREAM,
    EXTERNAL_APP,
    INFO_ONLY;

    val isDirectPlayable: Boolean
        get() = this == LOCAL || this == DIRECT_STREAM
}
