package org.ailingo.app.features.chathistory.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ConversationHistory(
    val conversationId: String,
    val topicName: String,
    val topicImage: String?,
    val lastMessageTimestamp: String,
    val isCompleted: Boolean
)