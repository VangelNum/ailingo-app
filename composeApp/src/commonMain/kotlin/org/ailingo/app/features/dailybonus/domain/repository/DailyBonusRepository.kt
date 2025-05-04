package org.ailingo.app.features.dailybonus.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.dailybonus.data.model.DailyBonusInfo

interface DailyBonusRepository {
    fun getDailyBonusInfo(): Flow<UiState<DailyBonusInfo>>
    fun claimDailyBonus(): Flow<UiState<DailyBonusInfo>>
}