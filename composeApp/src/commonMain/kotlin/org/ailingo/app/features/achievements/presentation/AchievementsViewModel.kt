package org.ailingo.app.features.achievements.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.achievements.data.model.Achievement
import org.ailingo.app.features.achievements.domain.repository.AchievementsRepository

class AchievementsViewModel(
    private val achievementsRepository: AchievementsRepository
) : ViewModel() {

    private var _achievementsState = MutableStateFlow<UiState<List<Achievement>>>(UiState.Idle())
    val achievementsState = _achievementsState.asStateFlow()

    private var _claimAchievementsState = MutableStateFlow<UiState<Boolean>>(UiState.Idle())
    val claimAchievementsState = _claimAchievementsState.asStateFlow()

    init {
        getAchievementsInfo()
    }

    fun onEvent(event: AchievementsEvent) {
        when (event) {
            is AchievementsEvent.OnClaimAchievement -> claimAchievement(event.achievementId)
            AchievementsEvent.OnGetAchievementsInfo -> getAchievementsInfo()
        }
    }

    private fun getAchievementsInfo() {
        _claimAchievementsState.update { UiState.Idle() }
        viewModelScope.launch {
            achievementsRepository.getAchievementsInfo().collect { state ->
                _achievementsState.update { state }
            }
        }
    }

    private fun claimAchievement(achievementId: Int) {
        viewModelScope.launch {
            achievementsRepository.claimAchievement(achievementId).collect { state ->
                _claimAchievementsState.update { state }
            }
        }
    }

}