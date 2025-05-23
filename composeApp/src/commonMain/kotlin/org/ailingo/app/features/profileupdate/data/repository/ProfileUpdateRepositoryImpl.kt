package org.ailingo.app.features.profileupdate.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.profileupdate.data.model.ProfileUpdateRequest
import org.ailingo.app.features.profileupdate.domain.repository.ProfileUpdateRepository

class ProfileUpdateRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
) : ProfileUpdateRepository {
    override fun updateProfile(profileUpdateRequest: ProfileUpdateRequest): Flow<UiState<User>> =
        flow {
            emit(UiState.Loading())
            try {
                val response = httpClient.put("$BASE_URL/api/v1/user") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(profileUpdateRequest)
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