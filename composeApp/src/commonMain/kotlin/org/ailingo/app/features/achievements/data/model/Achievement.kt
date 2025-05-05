package org.ailingo.app.features.achievements.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    val type: String,
    val coins: Int,
    val xp: Int,
    val claimed: Boolean,
    val isAvailable: Boolean,
    val achievementId: Int?,
    val description: String,
    val imageUrl: String,
    val claimDate: String?
)