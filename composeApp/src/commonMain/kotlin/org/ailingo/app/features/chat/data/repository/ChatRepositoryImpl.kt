package org.ailingo.app.features.chat.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.chat.data.model.Conversation
import org.ailingo.app.features.chat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
) : ChatRepository {
    override fun startChat(topicName: String): Flow<UiState<Conversation>> = flow {
        emit(UiState.Loading())
        try {
            val encodedTopicName = topicName.encodeURLPath()
            val response = httpClient.post("$BASE_URL/api/v1/conversations/${encodedTopicName}") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body<Conversation>()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override fun startCustomChat(topicIdea: String): Flow<UiState<Conversation>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.post("$BASE_URL/api/v1/conversations/custom") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(topicIdea)
            }
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body<Conversation>()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override fun sendMessage(conversationId: String, message: String): Flow<UiState<Conversation>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.post("$BASE_URL/api/v1/conversations/continue/${conversationId}") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(message)
            }
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body<Conversation>()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override fun getMessagesFromSelectedChat(conversationId: String): Flow<UiState<List<Conversation>>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.get("$BASE_URL/api/v1/conversations/${conversationId}")
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override fun checkSingleMessage(userMessage: String): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.post("$BASE_URL/api/v1/conversations/grammarCheck") {
                setBody(userMessage)
            }
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }
}