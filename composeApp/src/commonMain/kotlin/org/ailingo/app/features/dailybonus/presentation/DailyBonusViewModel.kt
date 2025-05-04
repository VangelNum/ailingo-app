package org.ailingo.app.features.dailybonus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.dailybonus.data.model.DailyBonusInfo
import org.ailingo.app.features.dailybonus.domain.repository.DailyBonusRepository

class DailyBonusViewModel(
    private val dailyBonusRepository: DailyBonusRepository
) : ViewModel() {

    private val _dailyBonusInfoState = MutableStateFlow<UiState<DailyBonusInfo>>(UiState.Idle())
    val dailyBonusInfoState = _dailyBonusInfoState.asStateFlow()

    private val _claimDailyBonusInfoState =
        MutableStateFlow<UiState<DailyBonusInfo>>(UiState.Idle())
    val claimDailyBonusInfoState = _claimDailyBonusInfoState.asStateFlow()

    init {
        getDailyBonusInfo()
    }

    fun onEvent(event: DailyBonusEvent) {
        when (event) {
            DailyBonusEvent.OnGetDailyBonusInfo -> getDailyBonusInfo()
            DailyBonusEvent.OnClaimDailyBonus -> claimDailyBonus()
        }
    }

    private fun claimDailyBonus() {
        viewModelScope.launch {
            dailyBonusRepository.claimDailyBonus().collect { state ->
                _claimDailyBonusInfoState.update { state }
            }
        }
    }

    private fun getDailyBonusInfo() {
        _claimDailyBonusInfoState.update { UiState.Idle() }
        viewModelScope.launch {
            dailyBonusRepository.getDailyBonusInfo().collect { state ->
                _dailyBonusInfoState.update { state }
            }
        }
    }
}