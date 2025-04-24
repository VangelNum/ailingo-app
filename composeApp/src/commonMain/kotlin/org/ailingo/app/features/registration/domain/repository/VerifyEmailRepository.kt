package org.ailingo.app.features.registration.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User

interface VerifyEmailRepository {
    fun verifyEmail(email: String, verificationCode: String): Flow<UiState<User>>
}