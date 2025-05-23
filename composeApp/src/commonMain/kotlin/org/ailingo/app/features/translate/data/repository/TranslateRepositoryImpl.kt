package org.ailingo.app.features.translate.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.translate.domain.repository.TranslateRepository

class TranslateRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
) : TranslateRepository {
    override fun translate(text: String): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.get("$BASE_URL/api/v1/translate/") {
                parameter("text", text)
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