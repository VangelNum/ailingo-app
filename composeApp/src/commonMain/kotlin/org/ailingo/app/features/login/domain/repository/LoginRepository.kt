package org.ailingo.app.features.login.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User

interface LoginRepository {
    fun loginUser(login: String, password: String): Flow<UiState<User>>
    fun autoLogin(): Flow<UiState<User>>
}