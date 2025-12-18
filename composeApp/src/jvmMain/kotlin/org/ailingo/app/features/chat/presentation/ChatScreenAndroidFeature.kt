package org.ailingo.app.features.chat.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chat.data.model.Conversation

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
actual fun ChatScreenAndroidFeature(
    topicName: String,
    topicImage: String,
    chatUiState: UiState<MutableList<Conversation>>,
    messagesState: List<Conversation>,
    translateState: UiState<String>,
    singleMessageCheckState: UiState<String>,
    onEvent: (ChatEvents) -> Unit,
    userAvatar: String?,
    onNavigateToAnalyzeConversation: () -> Unit
) {
    Text("Lol only for android. what are doing here?")
}