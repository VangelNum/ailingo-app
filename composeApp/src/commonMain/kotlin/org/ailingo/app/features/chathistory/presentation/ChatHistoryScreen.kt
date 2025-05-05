package org.ailingo.app.features.chathistory.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.chat_history_empty
import ailingo.composeapp.generated.resources.emptystate
import ailingo.composeapp.generated.resources.errorstate
import ailingo.composeapp.generated.resources.loadingstate
import ailingo.composeapp.generated.resources.topic_is_finished
import ailingo.composeapp.generated.resources.topic_is_not_finished
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.ailingo.app.core.presentation.EmptyScreen
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.chathistory.data.model.ConversationHistory
import org.ailingo.app.features.topics.presentation.DEFAULT_IMAGE_URL
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatHistoryScreen(
    chatHistoryState: UiState<List<ConversationHistory>>,
    onNavigateToSelectedChat: (conversationId: String, topicName: String, topicImage: String) -> Unit
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(conversations) { conversation ->
                        ConversationHistoryItem(conversation, onNavigateToSelectedChat)
                    }
                }
            }
        }
    }
}


@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun ConversationHistoryItem(
    conversation: ConversationHistory,
    onNavigateToSelectedChat: (conversationId: String, topicName: String, topicImage: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTimestamp = try {
        val instant = Instant.parse(conversation.lastMessageTimestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val formatter = LocalDateTime.Format {
            byUnicodePattern("yyyy-MM-dd HH:mm")
        }
        localDateTime.format(formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        "Invalid date"
    }

    val isTopicComplete = conversation.isCompleted
    val topicStatusText = if (isTopicComplete) Res.string.topic_is_finished else Res.string.topic_is_not_finished
    val statusColor = if (isTopicComplete) MaterialTheme.colorScheme.primary else Color(0xFFFFA07A)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToSelectedChat(conversation.conversationId, conversation.topicName, conversation.topicImage ?: DEFAULT_IMAGE_URL)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = conversation.topicImage ?: DEFAULT_IMAGE_URL,
                contentDescription = conversation.topicName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = conversation.topicName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTimestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isTopicComplete) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                        contentDescription = if (isTopicComplete) "Topic Complete" else "Topic Incomplete",
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(topicStatusText),
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View conversation",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}