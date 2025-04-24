package org.ailingo.app.features.updateavatar.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponse(
    val data: UploadImageData,
    val success: Boolean,
    val status: Int
)