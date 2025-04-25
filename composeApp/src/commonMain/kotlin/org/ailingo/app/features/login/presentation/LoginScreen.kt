package org.ailingo.app.features.login.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.dont_have_an_account
import ailingo.composeapp.generated.resources.email
import ailingo.composeapp.generated.resources.enter_password
import ailingo.composeapp.generated.resources.enter_your_email
import ailingo.composeapp.generated.resources.login
import ailingo.composeapp.generated.resources.login_to_countinue
import ailingo.composeapp.generated.resources.password
import ailingo.composeapp.generated.resources.visibility
import ailingo.composeapp.generated.resources.visibility_off
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.SmallLoadingIndicator
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomAuthTextField
import org.ailingo.app.core.presentation.custom.CustomButton
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.registration.presentation.VerticalSpacer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    loginState: UiState<User>,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onEvent: (LoginEvent) -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Error) {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = loginState.message
                )
            )
            onEvent(LoginEvent.OnBackToEmptyState)
        }
        if (loginState is UiState.Success) {
            onNavigateToHomeScreen()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            VerticalSpacer(12.dp)
            Text(
                stringResource(Res.string.login),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            VerticalSpacer(4.dp)
            Text(
                stringResource(Res.string.login_to_countinue),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            VerticalSpacer(16.dp)

            CustomAuthTextField(
                labelResId = Res.string.email,
                placeholderResId = Res.string.enter_your_email,
                value = email.value.trim(),
                onValueChange = { email.value = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
                focusRequester = focusRequesterEmail
            )
            VerticalSpacer(16.dp)

            CustomAuthTextField(
                labelResId = Res.string.password,
                placeholderResId = Res.string.enter_password,
                value = password.value.trim(),
                onValueChange = { password.value = it },
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onEvent(LoginEvent.OnLoginUser(email.value.trim(), password.value.trim()))
                }),
                focusRequester = focusRequesterPassword,
                trailingIcon = {
                    val image =
                        if (passwordVisible.value) Res.drawable.visibility else Res.drawable.visibility_off
                    IconButton(onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }) {
                        Icon(painter = painterResource(image), contentDescription = null)
                    }
                }
            )
            VerticalSpacer(16.dp)
            CustomButton(
                onClick = {
                    onEvent(LoginEvent.OnLoginUser(email.value, password.value))
                },
                enabled = loginState !is UiState.Loading
            ) {
                Text(
                    stringResource(Res.string.login),
                    style = MaterialTheme.typography.titleLarge
                )
                if (loginState is UiState.Loading) {
                    Spacer(modifier = Modifier.width(16.dp))
                    SmallLoadingIndicator(color = Color.Green)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                stringResource(Res.string.dont_have_an_account), modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        onNavigateToRegisterScreen()
                    }
            )
        }
    }
}