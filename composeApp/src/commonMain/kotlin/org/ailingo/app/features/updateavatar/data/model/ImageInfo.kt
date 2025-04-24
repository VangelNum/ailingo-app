package org.ailingo.app.features.updateavatar.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageInfo(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)