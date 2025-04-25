package org.ailingo.app.features.leaderboard.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.leaderboard.data.model.Leaderboard
import org.ailingo.app.features.leaderboard.domain.repository.LeaderboardRepository

class LeaderboardRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
) : LeaderboardRepository {
    override fun getLeaderboard(): Flow<UiState<List<Leaderboard>>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.get("$BASE_URL/api/v1/leaderboard")
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body<List<Leaderboard>>()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }
}