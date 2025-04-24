package org.ailingo.app.features.uploadimage.data.repository

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
import org.ailingo.app.features.uploadimage.data.model.UploadImageRequest
import org.ailingo.app.features.uploadimage.data.model.UploadImageResponse
import org.ailingo.app.features.uploadimage.domain.repository.UploadImageRepository

class UploadImageRepositoryImpl(
    private val httpClient: HttpClient,
    private val errorMapper: ErrorMapper
): UploadImageRepository {

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

}