package org.ailingo.app.features.registration.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.registration.data.model.EmailVerifyRequest
import org.ailingo.app.features.registration.domain.repository.VerifyEmailRepository

class VerifyEmailRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorParser: ErrorMapper
) : VerifyEmailRepository {

    override fun verifyEmail(email: String, verificationCode: String): Flow<UiState<User>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.post("$BASE_URL/api/v1/user/verify-email") {
                contentType(ContentType.Application.Json)
                setBody(EmailVerifyRequest(email, verificationCode))
            }
            if (response.status.isSuccess()) {
                val authResponse = response.body<User>()
                emit(UiState.Success(authResponse))
            } else {
                emit(UiState.Error(errorParser.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.mapError(e)))
        }
    }
}