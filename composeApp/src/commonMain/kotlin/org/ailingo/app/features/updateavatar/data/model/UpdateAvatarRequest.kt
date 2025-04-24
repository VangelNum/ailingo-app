package org.ailingo.app.features.updateavatar.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarRequest(
    val avatarUrl: String
)