package org.ailingo.app.features.leaderboard.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.leaderboard.data.model.Leaderboard

interface LeaderboardRepository {
    fun getLeaderboard(): Flow<UiState<List<Leaderboard>>>
}