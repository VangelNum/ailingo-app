package org.ailingo.app.features.leaderboard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Leaderboard(
    val coins: Int,
    val streak: Int,
    val avatar: String?,
    val name: String
)