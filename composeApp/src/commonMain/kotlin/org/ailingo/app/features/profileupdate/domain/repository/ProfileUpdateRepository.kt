package org.ailingo.app.features.profileupdate.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.profileupdate.data.model.ProfileUpdateRequest

interface ProfileUpdateRepository {
    fun updateProfile(profileUpdateRequest: ProfileUpdateRequest): Flow<UiState<User>>
}