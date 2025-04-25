package org.ailingo.app.features.chathistory.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chathistory.data.model.ConversationHistory

interface ChatHistoryRepository {
    fun getConversations(): Flow<UiState<List<ConversationHistory>>>
}