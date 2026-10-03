package com.aruack.music.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LicenseInfo(
    val licenseName: String = "All Rights Reserved",
    val licenseUrl: String? = null,
    val isCreativeCommons: Boolean = false,
    val isPublicDomain: Boolean = false,
    val attributionRequired: Boolean = false,
    val licenseCode: String? = null
)
