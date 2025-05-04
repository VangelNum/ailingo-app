package org.ailingo.app.features.dailybonus.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.dailybonus.data.model.DailyBonusInfo
import org.ailingo.app.features.dailybonus.domain.repository.DailyBonusRepository

class DailyBonusRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
): DailyBonusRepository {
    override fun getDailyBonusInfo(): Flow<UiState<DailyBonusInfo>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.get("$BASE_URL/api/v1/user/daily-login/status")
            if (response.status.isSuccess()) {
                emit(UiState.Success(response.body()))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override fun claimDailyBonus(): Flow<UiState<DailyBonusInfo>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.post("$BASE_URL/api/v1/user/daily-login/claim")
            if (response.status.isSuccess()) {
                val dailyBonusInfo: DailyBonusInfo = response.body()
                emit(UiState.Success(dailyBonusInfo))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }
}