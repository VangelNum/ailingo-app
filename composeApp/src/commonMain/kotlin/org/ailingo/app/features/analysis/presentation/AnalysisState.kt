package org.ailingo.app.features.analysis.presentation

import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.data.model.AnalysisInfo

data class AnalysisState(
    val basicGrammarState: UiState<List<AnalysisInfo>> = UiState.Idle(),
    val vocabularyPhrasingState: UiState<List<AnalysisInfo>> = UiState.Idle(),
    val clarityStyleState: UiState<List<AnalysisInfo>> = UiState.Idle(),
    val beginnerErrorsState: UiState<List<AnalysisInfo>> = UiState.Idle()
)