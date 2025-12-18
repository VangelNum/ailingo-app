package org.ailingo.app.features.chat.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chat.data.model.Conversation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
expect fun ChatScreenAndroidFeature(
    topicName: String,
    topicImage: String,
    chatUiState: UiState<MutableList<Conversation>>,
    messagesState: List<Conversation>,
    translateState: UiState<String>,
    singleMessageCheckState: UiState<String>,
    onEvent: (ChatEvents) -> Unit,
    userAvatar: String?,
    onNavigateToAnalyzeConversation: () -> Unit,
)