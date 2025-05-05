package org.ailingo.app.features.achievements.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.achievements.data.model.Achievement
import org.ailingo.app.features.achievements.domain.repository.AchievementsRepository

class AchievementsRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
): AchievementsRepository {
    override fun getAchievementsInfo(): Flow<UiState<List<Achievement>>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.get("$BASE_URL/api/v1/user/achievements")
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(throwable = e)))
        }
    }

    override fun claimAchievement(achievementId: Int): Flow<UiState<Boolean>> = flow {
        emit(UiState.Loading())
        try {
            val response: HttpResponse = httpClient.post("$BASE_URL/api/v1/user/achievements/$achievementId/claim")
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(throwable = e)))
        }
    }
}