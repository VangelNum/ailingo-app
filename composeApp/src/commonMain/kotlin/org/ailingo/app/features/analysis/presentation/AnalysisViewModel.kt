package org.ailingo.app.features.analysis.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.data.model.AnalysisInfo
import org.ailingo.app.features.analysis.domain.repository.AnalysisRepository

class AnalysisViewModel(
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private val _basicGrammarState = MutableStateFlow<UiState<List<AnalysisInfo>>>(UiState.Idle())
    val basicGrammarState = _basicGrammarState.asStateFlow()

    fun onEvent(event: AnalysisEvent) {
        when (event) {
            is AnalysisEvent.OnCheckBasicGrammar -> analysisBaseGrammar(event.conversationId)
        }
    }

    private fun analysisBaseGrammar(conversationId: String) {
        viewModelScope.launch {
            analysisRepository.analysisBasicGrammar(conversationId).collect { state ->
                _basicGrammarState.update { state }
            }
        }
    }
}