package org.ailingo.app.features.updateavatar.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageRequest(
    val image: String,
    val name: String?,
    val expiration: Int?
)