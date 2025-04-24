package org.ailingo.app.features.login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.basicauth.domain.repository.AuthRepository
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.login.domain.repository.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle())
    val loginState = _loginState.asStateFlow()

    var login by mutableStateOf("")
    var password by mutableStateOf("")

    init {
        autoLogin()
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnLoginUser -> loginUser(event.login, event.password)
            LoginEvent.OnBackToEmptyState -> backToEmptyState()
            LoginEvent.OnRefreshUserInfo -> autoLogin()
        }
    }

    private fun autoLogin() {
        viewModelScope.launch {
            loginRepository.autoLogin().collect { state ->
                _loginState.update { state }
            }
        }
    }

    private fun loginUser(
        login: String,
        password: String
    ) {
        viewModelScope.launch {
            loginRepository.loginUser(login, password).collect { state ->
                _loginState.update { state }
            }
        }
    }

    private fun backToEmptyState() {
        viewModelScope.launch {
            authRepository.deleteBasicAuth()
        }
        _loginState.update {
            UiState.Idle()
        }
    }
}