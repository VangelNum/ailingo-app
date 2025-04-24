package org.ailingo.app.features.uploadimage.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.uploadimage.data.model.UploadImageRequest
import org.ailingo.app.features.uploadimage.data.model.UploadImageResponse

interface UploadImageRepository {
    fun uploadImage(uploadImageRequest: UploadImageRequest): Flow<UiState<UploadImageResponse>>
}