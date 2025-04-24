package org.ailingo.app.features.updateavatar.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User

interface UpdateAvatarRepository {
    fun updateUserAvatar(avatarUrl: String): Flow<UiState<User>>
    fun generateAvatar(): Flow<UiState<String>>
}