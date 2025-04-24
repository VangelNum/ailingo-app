package org.ailingo.app.features.updateavatar.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.error_loading_image
import ailingo.composeapp.generated.resources.error_updating_avatar
import ailingo.composeapp.generated.resources.image_uploaded_successfully
import ailingo.composeapp.generated.resources.update_avatar_choose_generate
import ailingo.composeapp.generated.resources.update_avatar_choose_photo
import ailingo.composeapp.generated.resources.update_avatar_generate_other
import ailingo.composeapp.generated.resources.update_avatar_lets_set_photo
import ailingo.composeapp.generated.resources.update_avatar_proceed
import ailingo.composeapp.generated.resources.update_avatar_successful
import ailingo.composeapp.generated.resources.update_avatar_successful_registration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.SmallLoadingIndicator
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomOutlinedButton
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.profileupdate.presentation.selectImage
import org.ailingo.app.features.uploadimage.data.model.UploadImageResponse
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateAvatarScreen(
    uploadAvatarState: UiState<UploadImageResponse>,
    updateAvatarState: UiState<User>,
    generatedAvatarsState: List<UiState<String>>,
    onEvent: (UpdateAvatarEvent) -> Unit,
    onNavigateToBunsScreen: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var localeSelectedImageBase64 by remember {
        mutableStateOf<String?>(null)
    }

    var selectedImageUri by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(localeSelectedImageBase64) {
        localeSelectedImageBase64?.let { onEvent(UpdateAvatarEvent.OnUploadImage(it)) }
    }

    LaunchedEffect(uploadAvatarState) {
        if (uploadAvatarState is UiState.Error) {
            SnackbarController.sendEvent(
                SnackbarEvent(
                    message = getString(Res.string.error_loading_image, uploadAvatarState.message)
                )
            )
        } else if (uploadAvatarState is UiState.Success) {
            selectedImageUri = uploadAvatarState.data.data.display_url
            SnackbarController.sendEvent(SnackbarEvent(message = getString(Res.string.image_uploaded_successfully)))
        }
    }

    LaunchedEffect(updateAvatarState) {
        if (updateAvatarState is UiState.Error) {
            SnackbarController.sendEvent(
                SnackbarEvent(
                    message = getString(Res.string.error_updating_avatar, updateAvatarState.message)
                )
            )
        } else if (updateAvatarState is UiState.Success) {
            SnackbarController.sendEvent(SnackbarEvent(message = getString(Res.string.update_avatar_successful)))
            onNavigateToBunsScreen()
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.update_avatar_successful_registration),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.update_avatar_lets_set_photo),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = CircleShape,
                modifier = Modifier.size(150.dp),
            ) {
                if (selectedImageUri != null) {
                    SubcomposeAsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                SmallLoadingIndicator()
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Failed")
                            }
                        }
                    )
                } else {
                    if (uploadAvatarState is UiState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            SmallLoadingIndicator()
                        }
                    } else {
                        Image(
                            painterResource(Res.drawable.defaultProfilePhoto),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.update_avatar_choose_generate),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                generatedAvatarsState.forEachIndexed { index, state ->
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(enabled = state is UiState.Success && uploadAvatarState !is UiState.Loading) {
                                if (state is UiState.Success) {
                                    selectedImageUri = state.data
                                }
                            },
                        border = if (state is UiState.Success && state.data == selectedImageUri) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
                        elevation = CardDefaults.cardElevation(defaultElevation = if (state is UiState.Success && state.data == selectedImageUri) 4.dp else 1.dp)
                    ) {
                        when (state) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SmallLoadingIndicator()
                                }
                            }

                            is UiState.Success -> {
                                SubcomposeAsyncImage(
                                    model = state.data,
                                    contentDescription = "Generated Avatar ${index + 1}",
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            SmallLoadingIndicator()
                                        }
                                    },
                                    error = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Error",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Red
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            is UiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Failed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomOutlinedButton(
                onClick = {
                    scope.launch {
                        localeSelectedImageBase64 = selectImage()
                    }
                },
                enabled = uploadAvatarState !is UiState.Loading && updateAvatarState !is UiState.Loading
            ) {
                Text(stringResource(Res.string.update_avatar_choose_photo))
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedButton(
                onClick = { onEvent(UpdateAvatarEvent.OnGenerateAvatar) },
                enabled = generatedAvatarsState.all { it !is UiState.Loading } && uploadAvatarState !is UiState.Loading && updateAvatarState !is UiState.Loading
            ) {
                val isGenerating = generatedAvatarsState.any { it is UiState.Loading }
                if (isGenerating) {
                    SmallLoadingIndicator(color = MaterialTheme.colorScheme.onSurface)
                } else {
                    Text(stringResource(Res.string.update_avatar_generate_other))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedButton(
                onClick = {
                    if (selectedImageUri != null) {
                        onEvent(UpdateAvatarEvent.OnUpdateUserAvatar(selectedImageUri!!))
                    } else {
                        onNavigateToBunsScreen()
                    }
                },
                enabled = updateAvatarState !is UiState.Loading
            ) {
                if (updateAvatarState is UiState.Loading) {
                    SmallLoadingIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(Res.string.update_avatar_proceed))
                }
            }
        }
    }
}