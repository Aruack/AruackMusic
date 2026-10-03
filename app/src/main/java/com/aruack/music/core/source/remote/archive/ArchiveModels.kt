package com.aruack.music.core.source.remote.archive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArchiveSearchResponse(
    val responseHeader: ArchiveResponseHeader? = null,
    val response: ArchiveResponseBody? = null
)

@Serializable
data class ArchiveResponseHeader(
    val status: Int = 0,
    @SerialName("QTime") val qTime: Int = 0
)

@Serializable
data class ArchiveResponseBody(
    val numFound: Int = 0,
    val start: Int = 0,
    val docs: List<ArchiveDoc> = emptyList()
)

@Serializable
data class ArchiveDoc(
    val identifier: String,
    val title: String? = null,
    val creator: String? = null,
    val date: String? = null,
    val description: String? = null,
    val mediatype: String? = null,
    val collection: List<String> = emptyList(),
    val year: String? = null
)
