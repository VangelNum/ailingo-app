package org.ailingo.app.features.login.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.basicauth.domain.repository.AuthRepository
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.login.domain.repository.LoginRepository
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LoginRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper,
    private val authRepository: AuthRepository
) : LoginRepository {
    @OptIn(ExperimentalEncodingApi::class)
    override fun loginUser(login: String, password: String): Flow<UiState<User>> = flow {
        emit(UiState.Loading())
        try {
            val credentials = "$login:$password"
            val encodedCredentials = Base64.encode(credentials.toByteArray(Charsets.UTF_8))
            val response: HttpResponse = httpClient.get("$BASE_URL/api/v1/user/me") {
                header(HttpHeaders.Authorization, "Basic $encodedCredentials")
            }
            if (response.status == HttpStatusCode.OK) {
                authRepository.saveBasicAuth(encodedCredentials)
                val user: User = response.body()
                emit(UiState.Success(user))
            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override fun autoLogin(): Flow<UiState<User>> = flow {
        val credentials = authRepository.getBasicAuth() ?: return@flow
        try {
            val response = httpClient.get("$BASE_URL/api/v1/user/me") {
                header(HttpHeaders.Authorization, "Basic $credentials")
            }
            if (response.status == HttpStatusCode.OK) {
                emit(UiState.Success(response.body()))
            } else {
                authRepository.deleteBasicAuth()
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            authRepository.deleteBasicAuth()
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }
}