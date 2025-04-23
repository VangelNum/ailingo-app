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
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.login.domain.repository.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle())
    val loginState = _loginState.asStateFlow()

    var login by mutableStateOf("")
    var password by mutableStateOf("")

    init {
        authLogin()
    }

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnLoginUser -> loginUser(event.login, event.password)
            LoginScreenEvent.OnBackToEmptyState -> backToEmptyState()
            LoginScreenEvent.OnAuthLogin -> authLogin()
        }
    }

    private fun authLogin() {
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
        _loginState.value = UiState.Idle()
    }
}