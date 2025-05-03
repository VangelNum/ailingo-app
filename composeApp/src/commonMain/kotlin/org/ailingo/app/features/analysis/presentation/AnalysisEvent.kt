package org.ailingo.app.features.analysis.presentation

sealed class AnalysisEvent {
    data class OnCheckBasicGrammar(val conversationId: String) : AnalysisEvent()
    data class OnCheckVocabularyPhrasing(val conversationId: String) : AnalysisEvent()
    data class OnCheckClarityStyle(val conversationId: String) : AnalysisEvent()
    data class OnCheckBeginnerErrors(val conversationId: String) : AnalysisEvent()
}