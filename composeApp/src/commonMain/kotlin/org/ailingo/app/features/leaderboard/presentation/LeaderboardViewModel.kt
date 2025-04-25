package org.ailingo.app.features.leaderboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.leaderboard.data.model.Leaderboard
import org.ailingo.app.features.leaderboard.domain.repository.LeaderboardRepository


class LeaderboardViewModel(
    private val leaderBoardRepository: LeaderboardRepository
) : ViewModel() {
    private val _leaderboardState = MutableStateFlow<UiState<List<Leaderboard>>>(UiState.Idle())
    val leaderboardState = _leaderboardState.asStateFlow()

    init {
        getLeaderboard()
    }

    private fun getLeaderboard() {
        viewModelScope.launch {
            leaderBoardRepository.getLeaderboard().collect { state ->
                _leaderboardState.update { state }
            }
        }
    }
}