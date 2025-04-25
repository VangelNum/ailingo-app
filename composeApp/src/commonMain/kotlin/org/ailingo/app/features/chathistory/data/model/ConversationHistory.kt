package org.ailingo.app.features.chathistory.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ConversationHistory(
    val conversationId: String,
    val topicName: String,
    val creationTimestamp: String
)