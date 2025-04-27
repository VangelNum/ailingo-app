package org.ailingo.app.features.profileupdate.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.avatar
import ailingo.composeapp.generated.resources.back
import ailingo.composeapp.generated.resources.change_of_personal_data
import ailingo.composeapp.generated.resources.change_user_data
import ailingo.composeapp.generated.resources.current_password
import ailingo.composeapp.generated.resources.data_changed_successfully
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.email
import ailingo.composeapp.generated.resources.enter_current_password
import ailingo.composeapp.generated.resources.enter_your_email
import ailingo.composeapp.generated.resources.enter_your_name
import ailingo.composeapp.generated.resources.enter_your_new_password
import ailingo.composeapp.generated.resources.name
import ailingo.composeapp.generated.resources.new_password
import ailingo.composeapp.generated.resources.visibility
import ailingo.composeapp.generated.resources.visibility_off
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.SubcomposeAsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomAuthTextField
import org.ailingo.app.core.presentation.custom.CustomButton
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.profileupdate.data.model.ProfileUpdateRequest
import org.ailingo.app.features.registration.presentation.VerticalSpacer
import org.ailingo.app.features.uploadimage.data.model.UploadImageResponse
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileUpdateScreen(
    profileUpdateUiState: UiState<User>,
    uploadAvatarState: UiState<UploadImageResponse>,
    onProfileUpdate: (ProfileUpdateRequest) -> Unit,
    onReLoginUser: (newLogin: String, currentPassword: String, newPassword: String, passwordChanged: Boolean) -> Unit,
    onNavigateProfileScreen: () -> Unit,
    onBackToEmptyState: () -> Unit,
    name: String,
    email: String,
    avatar: String?,
    onUploadNewAvatar: (String) -> Unit
) {
    val newName = remember {
        mutableStateOf(name)
    }

    val newEmail = remember {
        mutableStateOf(email)
    }

    val newPassword = remember {
        mutableStateOf("")
    }

    val currentPassword = remember {
        mutableStateOf("")
    }

    LaunchedEffect(profileUpdateUiState) {
        if (profileUpdateUiState is UiState.Success) {
            SnackbarController.sendEvent(SnackbarEvent(getString(Res.string.data_changed_successfully)))
            val passwordChanged = newPassword.value.isNotEmpty()
            onReLoginUser(newEmail.value, currentPassword.value, newPassword.value, passwordChanged)
            onNavigateProfileScreen()
        }
    }

    when (profileUpdateUiState) {
        is UiState.Idle -> {
            ProfileUpdateContent(
                onProfileUpdate,
                avatar,
                newName,
                newEmail,
                newPassword,
                currentPassword,
                onUploadNewAvatar,
                uploadAvatarState
            )
        }

        is UiState.Error -> {
            ErrorScreen(
                errorMessage = profileUpdateUiState.message,
                onButtonClick = onBackToEmptyState,
                buttonMessage = stringResource(Res.string.back),
                modifier = Modifier.fillMaxSize()
            )
        }

        is UiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }

        is UiState.Success -> {}
    }
}

@Composable
fun ProfileUpdateContent(
    onProfileUpdate: (ProfileUpdateRequest) -> Unit,
    avatar: String?,
    newName: MutableState<String>,
    newEmail: MutableState<String>,
    newPassword: MutableState<String>,
    currentPassword: MutableState<String>,
    onUploadNewAvatar: (String) -> Unit,
    uploadAvatarState: UiState<UploadImageResponse>
) {
    val currentPasswordVisible = remember { mutableStateOf(false) }
    val newPasswordVisible = remember { mutableStateOf(false) }
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterNewPassword = remember { FocusRequester() }
    val focusRequesterCurrentPassword = remember { FocusRequester() }

    val uploadedAvatarUrl = remember { mutableStateOf(avatar) }

    LaunchedEffect(uploadAvatarState) {
        if (uploadAvatarState is UiState.Success) {
            uploadedAvatarUrl.value = uploadAvatarState.data.data.display_url
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(Res.string.change_of_personal_data),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                VerticalSpacer(16.dp)

                AvatarProfile(avatar, onUploadNewAvatar, uploadAvatarState)

                VerticalSpacer(8.dp)

                CustomAuthTextField(
                    labelResId = Res.string.name,
                    placeholderResId = Res.string.enter_your_name,
                    value = newName.value,
                    onValueChange = {
                        newName.value = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
                    focusRequester = focusRequesterName
                )
                VerticalSpacer(8.dp)

                CustomAuthTextField(
                    labelResId = Res.string.email,
                    placeholderResId = Res.string.enter_your_email,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesterNewPassword.requestFocus() }),
                    value = newEmail.value,
                    onValueChange = {
                        newEmail.value = it
                    },
                    focusRequester = focusRequesterEmail,
                )

                VerticalSpacer(8.dp)

                CustomAuthTextField(
                    labelResId = Res.string.new_password,
                    placeholderResId = Res.string.enter_your_new_password,
                    visualTransformation = if (newPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesterCurrentPassword.requestFocus() }),
                    value = newPassword.value,
                    onValueChange = {
                        newPassword.value = it
                    },
                    focusRequester = focusRequesterNewPassword,
                    trailingIcon = {
                        val image =
                            if (newPasswordVisible.value) Res.drawable.visibility else Res.drawable.visibility_off
                        IconButton(onClick = {
                            newPasswordVisible.value = !newPasswordVisible.value
                        }) {
                            Icon(painter = painterResource(image), contentDescription = null)
                        }
                    }
                )

                VerticalSpacer(8.dp)

                CustomAuthTextField(
                    labelResId = Res.string.current_password,
                    placeholderResId = Res.string.enter_current_password,
                    visualTransformation = if (currentPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    value = currentPassword.value,
                    onValueChange = {
                        currentPassword.value = it
                    },
                    focusRequester = focusRequesterCurrentPassword,
                    trailingIcon = {
                        val image =
                            if (currentPasswordVisible.value) Res.drawable.visibility else Res.drawable.visibility_off
                        IconButton(onClick = {
                            currentPasswordVisible.value = !currentPasswordVisible.value
                        }) {
                            Icon(painter = painterResource(image), contentDescription = null)
                        }
                    }
                )

                VerticalSpacer(8.dp)
                CustomButton(
                    onClick = {
                        val newPasswordValue =
                            if (newPassword.value.isEmpty()) null else newPassword.value

                        onProfileUpdate(
                            ProfileUpdateRequest(
                                name = newName.value,
                                email = newEmail.value,
                                avatarUrl = uploadedAvatarUrl.value,
                                newPassword = newPasswordValue,
                                currentPassword = currentPassword.value
                            )
                        )
                    },
                    enabled = uploadAvatarState !is UiState.Loading &&
                            newName.value.isNotEmpty()
                            && newEmail.value.isNotEmpty()
                            && currentPassword.value.isNotEmpty()
                ) {
                    Text(
                        stringResource(Res.string.change_user_data),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                VerticalSpacer(32.dp)
            }
        }
    }
}

@Composable
fun AvatarProfile(
    avatar: String?,
    onUploadNewAvatar: (String) -> Unit,
    uploadAvatarState: UiState<UploadImageResponse>
) {

    val selectedImage = remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedImage.value) {
        if (selectedImage.value != null) {
            onUploadNewAvatar(selectedImage.value!!)
        }
    }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val modifier = if (adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT)
            Modifier.fillMaxWidth()
        else Modifier.width(OutlinedTextFieldDefaults.MinWidth)
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(Res.string.avatar))
            Spacer(modifier = Modifier.weight(1f))
            Card(
                shape = CircleShape
            ) {
                when (uploadAvatarState) {
                    is UiState.Error -> {
                        ErrorScreen(uploadAvatarState.message)
                    }

                    is UiState.Idle -> {
                        if (avatar != null) {
                            ProfileAvatarFromNetwork(scope, selectedImage, avatar)
                        } else {
                            DefaultProfileAvatar(scope, selectedImage)
                        }
                    }

                    is UiState.Loading -> {
                        ProfileAvatarLoading()
                    }

                    is UiState.Success -> {
                        ProfileAvatarFromNetwork(
                            scope,
                            selectedImage,
                            uploadAvatarState.data.data.display_url
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAvatarLoading() {
    Card(
        modifier = Modifier.fillMaxHeight()
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
    }
}

@Composable
fun DefaultProfileAvatar(scope: CoroutineScope, selectedImage: MutableState<String?>) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(Res.drawable.defaultProfilePhoto),
            modifier = Modifier.size(64.dp),
            contentDescription = null
        )
        IconButton(onClick = {
            scope.launch {
                selectedImage.value = selectImage()
            }
        }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
        }
    }
}

@Composable
fun ProfileAvatarFromNetwork(
    scope: CoroutineScope,
    selectedImage: MutableState<String?>,
    avatar: String?
) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        SubcomposeAsyncImage(
            model = avatar,
            modifier = Modifier.size(64.dp),
            contentDescription = null,
            loading = {
                CircularProgressIndicator()
            },
            contentScale = ContentScale.Crop
        )
        IconButton(onClick = {
            scope.launch {
                selectedImage.value = selectImage()
            }
        }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
        }
    }
}

expect suspend fun selectImage(): String?