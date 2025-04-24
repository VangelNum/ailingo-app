package org.ailingo.app.features.profileupdate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.profileupdate.data.model.ProfileUpdateRequest
import org.ailingo.app.features.profileupdate.domain.repository.ProfileUpdateRepository
import org.ailingo.app.features.uploadimage.data.model.UploadImageRequest
import org.ailingo.app.features.uploadimage.data.model.UploadImageResponse
import org.ailingo.app.features.uploadimage.domain.repository.UploadImageRepository

class ProfileUpdateViewModel(
    private val profileUpdateRepository: ProfileUpdateRepository,
    private val uploadImageRepository: UploadImageRepository
) : ViewModel() {
    private val _profileUpdateUiState = MutableStateFlow<UiState<User>>(UiState.Idle())
    val profileUpdateUiState = _profileUpdateUiState.asStateFlow()

    private val _uploadAvatarState = MutableStateFlow<UiState<UploadImageResponse>>(UiState.Idle())
    val uploadAvatarState = _uploadAvatarState.asStateFlow()

    fun onEvent(event: ProfileUpdateEvent) {
        when (event) {
            is ProfileUpdateEvent.OnUpdateProfile -> {
                updateProfile(event.profileUpdateRequest)
            }

            ProfileUpdateEvent.OnBackToEmptyState -> {
                backToEmptyState()
            }

            is ProfileUpdateEvent.OnUploadAvatar -> uploadAvatar(event.imageBase64)
        }
    }

    private fun uploadAvatar(imageBase64: String) {
        val request = UploadImageRequest(image = imageBase64, name = null, expiration = null)
        viewModelScope.launch {
            uploadImageRepository.uploadImage(request).collect { state ->
                _uploadAvatarState.update { state }
            }
        }
    }

    private fun updateProfile(profileUpdateRequest: ProfileUpdateRequest) {
        viewModelScope.launch {
            profileUpdateRepository.updateProfile(profileUpdateRequest).collect { state ->
                _profileUpdateUiState.update { state }
            }
        }
    }

    private fun backToEmptyState() {
        _profileUpdateUiState.update { UiState.Idle() }
    }
}