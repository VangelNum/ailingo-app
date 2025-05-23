package org.ailingo.app.features.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chat.data.model.Conversation

interface ChatRepository {
    fun startChat(topicName: String): Flow<UiState<Conversation>>
    fun startCustomChat(topicIdea: String): Flow<UiState<Conversation>>
    fun sendMessage(conversationId: String, message: String): Flow<UiState<Conversation>>
    
    fun getMessagesFromSelectedChat(conversationId: String): Flow<UiState<List<Conversation>>>

    fun checkSingleMessage(userMessage: String): Flow<UiState<String>>
}