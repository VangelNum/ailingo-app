package org.ailingo.app.features.login.presentation

sealed class LoginEvent {
    data class OnLoginUser(val login: String, val password: String) : LoginEvent()
    data object OnBackToEmptyState : LoginEvent()
    data object OnRefreshUserInfo: LoginEvent()
}