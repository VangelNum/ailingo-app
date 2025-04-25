package org.ailingo.app.features.chathistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chathistory.data.model.ConversationHistory
import org.ailingo.app.features.chathistory.domain.repository.ChatHistoryRepository

class ChatHistoryViewModel(
    private val chatHistoryRepository: ChatHistoryRepository
): ViewModel() {
    private val _chatHistoryState = MutableStateFlow<UiState<List<ConversationHistory>>>(UiState.Idle())
    val chatHistoryState = _chatHistoryState.asStateFlow()

    init {
        getChatHistory()
    }

    private fun getChatHistory() {
        viewModelScope.launch {
            chatHistoryRepository.getConversations().collect { state->
                _chatHistoryState.update { state }
            }
        }
    }
}