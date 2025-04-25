package org.ailingo.app.features.chathistory.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.chat_history_empty
import ailingo.composeapp.generated.resources.emptystate
import ailingo.composeapp.generated.resources.errorstate
import ailingo.composeapp.generated.resources.loadingstate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.EmptyScreen
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chathistory.data.model.ConversationHistory

@Composable
fun ChatHistoryScreen(
    chatHistoryState: UiState<List<ConversationHistory>>
) {
    when (chatHistoryState) {
        is UiState.Error -> {
            ErrorScreen(errorMessage = chatHistoryState.message, image = Res.drawable.errorstate)
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate)
        }

        is UiState.Success -> {
            val conversations = chatHistoryState.data
            if (conversations.isEmpty()) {
                EmptyScreen(
                    text = Res.string.chat_history_empty,
                    image = Res.drawable.emptystate,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(conversations, key = { it.conversationId }) { conversation ->
                        ConversationHistoryItem(conversation = conversation)
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationHistoryItem(conversation: ConversationHistory) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = conversation.topicName,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = conversation.creationTimestamp,
        )
    }
}