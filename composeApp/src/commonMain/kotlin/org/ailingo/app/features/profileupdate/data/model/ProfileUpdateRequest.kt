package org.ailingo.app.features.profileupdate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateRequest(
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val newPassword: String?,
    val currentPassword: String
)