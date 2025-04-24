package org.ailingo.app.features.registration.presentation.email_verification

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.back
import ailingo.composeapp.generated.resources.enter_code
import ailingo.composeapp.generated.resources.verify_email_subtitle
import ailingo.composeapp.generated.resources.verify_email_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.SmallLoadingIndicator
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomButton
import org.ailingo.app.core.presentation.custom.CustomElevatedButton
import org.ailingo.app.core.presentation.custom.CustomTextField
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.features.login.data.model.User
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerifyEmailScreen(
    email: String,
    registrationState: UiState<User>,
    onCodeCheck: (String) -> Unit,
    onNavigateToUpdateAvatar: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    LaunchedEffect(key1 = registrationState) {
        if (registrationState is UiState.Success) {
            onNavigateToUpdateAvatar()
        }
        if (registrationState is UiState.Error) {
            SnackbarController.sendEvent(SnackbarEvent(registrationState.message))
        }
    }

    var userInput by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(Res.string.verify_email_title, email),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(Res.string.verify_email_subtitle),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = userInput,
            onValueChange = {
                userInput = it
            },
            placeholderResId = Res.string.enter_code
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            onClick = {
                onCodeCheck(userInput)
            },
            enabled = userInput.isNotEmpty() && registrationState !is UiState.Loading
        ) {
            Row {
                Text("Проверить код")
                if (registrationState is UiState.Loading) {
                    Spacer(modifier = Modifier.width(16.dp))
                    SmallLoadingIndicator(color = Color.Green)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        CustomElevatedButton(onClick = {
            onNavigateBack()
        }) {
            Text(stringResource(Res.string.back))
        }
    }
}