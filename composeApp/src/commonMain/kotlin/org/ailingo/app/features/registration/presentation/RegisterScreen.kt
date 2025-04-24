package org.ailingo.app.features.registration.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.already_have_account
import ailingo.composeapp.generated.resources.continue_app
import ailingo.composeapp.generated.resources.email
import ailingo.composeapp.generated.resources.email_invalid
import ailingo.composeapp.generated.resources.enter_password
import ailingo.composeapp.generated.resources.enter_your_email
import ailingo.composeapp.generated.resources.enter_your_name
import ailingo.composeapp.generated.resources.name_invalid
import ailingo.composeapp.generated.resources.password
import ailingo.composeapp.generated.resources.password_invalid
import ailingo.composeapp.generated.resources.register_subtitle
import ailingo.composeapp.generated.resources.register_title
import ailingo.composeapp.generated.resources.username
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.SmallLoadingIndicator
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomAuthTextField
import org.ailingo.app.core.presentation.custom.CustomButton
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.features.registration.data.model.RegistrationRequest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegistrationScreen(
    onNavigateToLoginPage: () -> Unit,
    onNavigateToVerifyEmail: (email: String, password: String) -> Unit,
    pendingRegistrationState: UiState<String>,
    onEvent: (RegistrationEvent) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isNameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }

    RegistrationContent(
        name = name,
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        isNameValid = isNameValid,
        isEmailValid = isEmailValid,
        isPasswordValid = isPasswordValid,
        onNameChange = { newName ->
            name = newName.trim()
        },
        onEmailChange = { newEmail ->
            email = newEmail.trim()
        },
        onPasswordChange = { newPassword ->
            password = newPassword.trim()
        },
        onPasswordVisibleChange = { newPasswordVisible ->
            passwordVisible = newPasswordVisible
        },
        onNavigateToLoginPage = onNavigateToLoginPage,
        onRegisterClick = {
            isNameValid = name.length in 2..24 && name.isNotBlank()
            isEmailValid = isValidEmail(email)
            isPasswordValid = password.length in 8..24 && password.isNotBlank()

            if (isNameValid && isEmailValid && isPasswordValid) {
                onEvent(
                    RegistrationEvent.OnRegisterUser(
                        RegistrationRequest(name = name.trim(), password = password.trim(), email = email.trim())
                    )
                )
            }
        },
        pendingRegistrationState = pendingRegistrationState
    )

    LaunchedEffect(key1 = pendingRegistrationState) {
        if (pendingRegistrationState is UiState.Success) {
            onNavigateToVerifyEmail(email.trim().lowercase(), password.trim())
        }
    }
}

@Composable
fun RegistrationContent(
    name: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    isNameValid: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onNavigateToLoginPage: () -> Unit,
    onRegisterClick: () -> Unit,
    pendingRegistrationState: UiState<String>
) {
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    LaunchedEffect(pendingRegistrationState) {
        if (pendingRegistrationState is UiState.Error) {
            SnackbarController.sendEvent(
                SnackbarEvent(
                    message = pendingRegistrationState.message
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                stringResource(Res.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            VerticalSpacer(4.dp)
            Text(
                stringResource(Res.string.register_subtitle),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            VerticalSpacer(16.dp)

            CustomAuthTextField(
                labelResId = Res.string.username,
                placeholderResId = Res.string.enter_your_name,
                value = name,
                onValueChange = onNameChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
                focusRequester = focusRequesterName,
                isError = !isNameValid,
                errorMessage = if (!isNameValid) stringResource(Res.string.name_invalid) else null
            )
            VerticalSpacer(16.dp)

            CustomAuthTextField(
                labelResId = Res.string.email,
                placeholderResId = Res.string.enter_your_email,
                value = email,
                onValueChange = onEmailChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
                focusRequester = focusRequesterEmail,
                isError = !isEmailValid,
                errorMessage = if (!isEmailValid) stringResource(Res.string.email_invalid) else null
            )
            VerticalSpacer(16.dp)

            CustomAuthTextField(
                labelResId = Res.string.password,
                placeholderResId = Res.string.enter_password,
                value = password,
                onValueChange = onPasswordChange,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (isNameValid && isEmailValid && isPasswordValid) {
                        onRegisterClick()
                    }
                }),
                focusRequester = focusRequesterPassword,
                trailingIcon = {
                    val image =
                        if (passwordVisible) Res.drawable.visibility else Res.drawable.visibility_off
                    IconButton(onClick = {
                        onPasswordVisibleChange(!passwordVisible)
                    }) {
                        Icon(painter = painterResource(image), contentDescription = null)
                    }
                },
                isError = !isPasswordValid,
                errorMessage = if (!isPasswordValid) stringResource(Res.string.password_invalid) else null
            )
            VerticalSpacer(16.dp)
            CustomButton(
                onClick = {
                    onRegisterClick()
                },
                enabled = pendingRegistrationState !is UiState.Loading
            ) {
                Text(
                    stringResource(Res.string.continue_app),
                    style = MaterialTheme.typography.titleLarge
                )
                if (pendingRegistrationState is UiState.Loading) {
                    Spacer(modifier = Modifier.width(16.dp))
                    SmallLoadingIndicator(color = Color.Green)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                stringResource(Res.string.already_have_account), modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        onNavigateToLoginPage()
                    }
            )
        }
    }
}

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)(@{1})(.{1,})(\\.)(.{1,})"
    return email.matches(emailRegex.toRegex())
}