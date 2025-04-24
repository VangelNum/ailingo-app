package org.ailingo.app.features.updateavatar.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.defaultProfilePhoto
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import org.ailingo.app.features.updateavatar.data.model.UploadImageResponse
import org.jetbrains.compose.resources.painterResource

@Composable
fun UpdateAvatarScreen(
    uploadAvatarState: UiState<UploadImageResponse>,
    updateAvatarState: UiState<User>,
    generatedAvatarsState: List<UiState<String>>,
    selectedAvatarUrl: String?,
    locallySelectedBase64: String?,
    onEvent: (UpdateAvatarEvent) -> Unit,
    onNavigateToBunsScreen: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(uploadAvatarState) {
        if (uploadAvatarState is UiState.Error) {
            SnackbarController.sendEvent(SnackbarEvent(message = "Ошибка загрузки изображения: ${uploadAvatarState.message}"))
        } else if (uploadAvatarState is UiState.Success) {
            SnackbarController.sendEvent(SnackbarEvent(message = "Изображение успешно загружено!"))
        }
    }

    LaunchedEffect(updateAvatarState) {
        if (updateAvatarState is UiState.Error) {
            SnackbarController.sendEvent(SnackbarEvent(message = "Ошибка обновления аватара: ${updateAvatarState.message}"))
        } else if (updateAvatarState is UiState.Success) {
            SnackbarController.sendEvent(SnackbarEvent(message = "Аватар успешно установлен!"))
            onNavigateToBunsScreen()
        }
    }

    LaunchedEffect(generatedAvatarsState) {
        generatedAvatarsState.forEach { state ->
            if (state is UiState.Error) {
                SnackbarController.sendEvent(SnackbarEvent(message = "Ошибка генерации аватара: ${state.message}"))
            }
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Успешная регистрация",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Давайте установим фото",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = CircleShape,
                modifier = Modifier.size(150.dp),
            ) {
                val imageModel = locallySelectedBase64 ?: selectedAvatarUrl

                if (imageModel != null) {
                    SubcomposeAsyncImage(
                        model = imageModel,
                        contentDescription = "Selected Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            if (uploadAvatarState !is UiState.Loading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { SmallLoadingIndicator() }
                            }
                        },
                        error = {
                            if (uploadAvatarState !is UiState.Loading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { SmallLoadingIndicator() }
                            }
                        }
                    )
                } else {
                    Image(
                        painter = painterResource(Res.drawable.defaultProfilePhoto),
                        contentDescription = "Default profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (uploadAvatarState is UiState.Loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        SmallLoadingIndicator()
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Выберите или сгенерируйте",
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
                                    onEvent(UpdateAvatarEvent.OnSelectGeneratedAvatar(state.data))
                                }
                            },
                        border = if (state is UiState.Success && state.data == selectedAvatarUrl) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
                        elevation = CardDefaults.cardElevation(defaultElevation = if (state is UiState.Success && state.data == selectedAvatarUrl) 4.dp else 1.dp)
                    ) {
                        when (state) {
                            is UiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    SmallLoadingIndicator()
                                }
                            }

                            is UiState.Success -> {
                                SubcomposeAsyncImage(
                                    model = state.data,
                                    contentDescription = "Generated Avatar ${index + 1}",
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            SmallLoadingIndicator()
                                        }
                                    },
                                    error = {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text("Error", style = MaterialTheme.typography.labelSmall, color = Color.Red)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            is UiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Failed", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
                        val base64Image = selectImage()
                        if (base64Image != null) {
                            onEvent(UpdateAvatarEvent.OnUploadImage(base64Image))
                        }
                    }
                },
                enabled = uploadAvatarState !is UiState.Loading && updateAvatarState !is UiState.Loading
            ) {
                Text("Выбрать фото")
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
                    Text("Сгенерировать другие")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedButton(
                onClick = {
                    onEvent(UpdateAvatarEvent.OnUpdateUserAvatar)
                },
                enabled = selectedAvatarUrl != null && updateAvatarState !is UiState.Loading
            ) {
                if (updateAvatarState is UiState.Loading) {
                    SmallLoadingIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Продолжить")
                }
            }
        }
    }
}