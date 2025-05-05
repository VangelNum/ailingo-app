package org.ailingo.app.features.analysis.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.domain.repository.AnalysisRepository


class AnalysisViewModel(
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private val _analysisState = MutableStateFlow(AnalysisState())
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    fun onEvent(event: AnalysisEvent) {
        _analysisState.update { it.copy(basicGrammarState = UiState.Idle(), vocabularyPhrasingState = UiState.Idle(), clarityStyleState = UiState.Idle(), beginnerErrorsState = UiState.Idle()) }
        when (event) {
            is AnalysisEvent.OnCheckBasicGrammar -> analysisBasicGrammar(event.conversationId)
            is AnalysisEvent.OnCheckVocabularyPhrasing -> analysisVocabularyPhrasing(event.conversationId)
            is AnalysisEvent.OnCheckClarityStyle -> analysisClarityStyle(event.conversationId)
            is AnalysisEvent.OnCheckBeginnerErrors -> analysisBeginnerErrors(event.conversationId)
        }
    }

    private fun analysisBasicGrammar(conversationId: String) {
        viewModelScope.launch {
            analysisRepository.analysisBasicGrammar(conversationId).collect { state ->
                _analysisState.update { it.copy(basicGrammarState = state) }
            }
        }
    }

    private fun analysisVocabularyPhrasing(conversationId: String) {
        viewModelScope.launch {
            analysisRepository.analysisVocabularyPhrasing(conversationId).collect { state ->
                _analysisState.update { it.copy(vocabularyPhrasingState = state) }
            }
        }
    }

    private fun analysisClarityStyle(conversationId: String) {
        viewModelScope.launch {
            analysisRepository.analysisClarityStyle(conversationId).collect { state ->
                _analysisState.update { it.copy(clarityStyleState = state) }
            }
        }
    }

    private fun analysisBeginnerErrors(conversationId: String) {
        viewModelScope.launch {
            analysisRepository.analysisBeginnerErrors(conversationId).collect { state ->
                _analysisState.update { it.copy(beginnerErrorsState = state) }
            }
        }
    }
}