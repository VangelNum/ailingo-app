package org.ailingo.app.features.updateavatar.data.repository

import AiLingo.composeApp.BuildConfig.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPath
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.updateavatar.data.model.UpdateAvatarRequest
import org.ailingo.app.features.updateavatar.data.model.UploadImageRequest
import org.ailingo.app.features.updateavatar.data.model.UploadImageResponse
import org.ailingo.app.features.updateavatar.domain.repository.UpdateAvatarRepository
import kotlin.random.Random

class UpdateAvatarRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
) : UpdateAvatarRepository {
    override fun uploadImage(uploadImageRequest: UploadImageRequest): Flow<UiState<UploadImageResponse>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.post("$BASE_URL/api/v1/upload/image") {
                contentType(ContentType.Application.Json)
                setBody(uploadImageRequest)
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

    override fun updateUserAvatar(avatarUrl: String): Flow<UiState<User>> = flow {
        emit(UiState.Loading())
        try {
            val response = httpClient.put("$BASE_URL/api/v1/user/avatar") {
                contentType(ContentType.Application.Json)
                setBody(UpdateAvatarRequest(avatarUrl))
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


    override fun generateAvatar(): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        val prompt = "Generate user avatar"
        val width = 1024
        val height = 1024
        val nologo = true
        val safe = true

        val maxSeedValue = 999999999
        val seed = Random.nextInt(1, maxSeedValue)
        val encodedPrompt = prompt.encodeURLPath()

        val imageUrl = buildString {
            append(BASE_URL)
            append("/api/v1/generate/image/")
            append(encodedPrompt)
            append("?seed=")
            append(seed)
            append("&width=")
            append(width)
            append("&height=")
            append(height)
            append("&nologo=")
            append(nologo)
            append("&safe=")
            append(safe)
        }

        try {
            val response = httpClient.get("$BASE_URL/api/v1/generate/image/$encodedPrompt") {
                parameter("seed", seed)
                parameter("width", width)
                parameter("height", height)
                parameter("nologo", nologo)
                parameter("safe", safe)
            }

            if (response.status.isSuccess()) {
                emit(UiState.Success(imageUrl))

            } else {
                emit(UiState.Error(errorMapper.mapError(httpResponse = response)))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }
}