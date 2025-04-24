package org.ailingo.app.features.updateavatar.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.updateavatar.data.model.UploadImageRequest
import org.ailingo.app.features.updateavatar.data.model.UploadImageResponse

interface UpdateAvatarRepository {
    fun uploadImage(uploadImageRequest: UploadImageRequest): Flow<UiState<UploadImageResponse>>
    fun updateUserAvatar(avatarUrl: String): Flow<UiState<User>>
    fun generateAvatar(): Flow<UiState<String>>
}