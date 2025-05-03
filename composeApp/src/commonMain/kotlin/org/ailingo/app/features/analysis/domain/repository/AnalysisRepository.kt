package org.ailingo.app.features.analysis.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.data.model.AnalysisInfo

interface AnalysisRepository {
    fun analysisBasicGrammar(conversationId: String): Flow<UiState<List<AnalysisInfo>>>
    fun analysisVocabularyPhrasing(conversationId: String): Flow<UiState<List<AnalysisInfo>>>
    fun analysisClarityStyle(conversationId: String): Flow<UiState<List<AnalysisInfo>>>
    fun analysisBeginnerErrors(conversationId: String): Flow<UiState<List<AnalysisInfo>>>
}