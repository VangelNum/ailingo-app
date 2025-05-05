package org.ailingo.app.features.achievements.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.achievements.data.model.Achievement

interface AchievementsRepository {
    fun getAchievementsInfo(): Flow<UiState<List<Achievement>>>
    fun claimAchievement(achievementId: Int): Flow<UiState<Boolean>>
}