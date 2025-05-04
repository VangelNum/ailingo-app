package org.ailingo.app.features.dailybonus.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyBonusInfo(
    val streak: Int,
    val coinsRewarded: Int,
    val message: String,
    val totalRemainingTimeSeconds: Long,
    val isAvailable: Boolean
)