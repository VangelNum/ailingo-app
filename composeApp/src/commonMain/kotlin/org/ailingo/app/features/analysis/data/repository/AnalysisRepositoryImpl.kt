package org.ailingo.app.features.analysis.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.analysis.data.model.AnalysisInfo
import org.ailingo.app.features.analysis.domain.repository.AnalysisRepository

class AnalysisRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
) : AnalysisRepository {
    override fun analysisBasicGrammar(conversationId: String): Flow<UiState<List<AnalysisInfo>>> = flow {
        emit(UiState.Loading())
        try {
            val response =
                httpClient.post("$BASE_URL/api/v1/conversations/$conversationId/analyze/basic-grammar") {
                    contentType(ContentType.Application.Json)
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