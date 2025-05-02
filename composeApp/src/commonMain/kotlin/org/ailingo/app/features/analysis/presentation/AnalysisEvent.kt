package org.ailingo.app.features.analysis.presentation

sealed class AnalysisEvent {
    data class OnCheckBasicGrammar(val conversationId: String) : AnalysisEvent()
}