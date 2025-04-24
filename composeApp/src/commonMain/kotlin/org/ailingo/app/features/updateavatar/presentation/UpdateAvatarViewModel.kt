package org.ailingo.app.features.updateavatar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.login.data.model.User
import org.ailingo.app.features.updateavatar.data.model.UploadImageRequest
import org.ailingo.app.features.updateavatar.data.model.UploadImageResponse
import org.ailingo.app.features.updateavatar.domain.repository.UpdateAvatarRepository

class UpdateAvatarViewModel(
    private val updateAvatarRepository: UpdateAvatarRepository
) : ViewModel() {

    private val _uploadAvatarState = MutableStateFlow<UiState<UploadImageResponse>>(UiState.Idle())
    val uploadAvatarState = _uploadAvatarState.asStateFlow()

    private val _updateAvatarState = MutableStateFlow<UiState<User>>(UiState.Idle())
    val updateAvatarState = _updateAvatarState.asStateFlow()

    private val _generatedAvatarsState = MutableStateFlow<List<UiState<String>>>(List(3) { UiState.Idle() })
    val generatedAvatarsState = _generatedAvatarsState.asStateFlow()

    private val _selectedAvatarUrl = MutableStateFlow<String?>(null)
    val selectedAvatarUrl = _selectedAvatarUrl.asStateFlow()

    private val _locallySelectedBase64 = MutableStateFlow<String?>(null)
    val locallySelectedBase64 = _locallySelectedBase64.asStateFlow()

    fun onEvent(event: UpdateAvatarEvent) {
        when (event) {
            is UpdateAvatarEvent.OnUpdateUserAvatar -> {
                _selectedAvatarUrl.value?.let { url ->
                    updateUserAvatar(url)
                }
            }
            is UpdateAvatarEvent.OnUploadImage -> uploadImage(event.base64Image)
            UpdateAvatarEvent.OnGenerateAvatar -> generateAvatars()
            is UpdateAvatarEvent.OnSelectGeneratedAvatar -> selectGeneratedAvatar(event.avatarUrl)
        }
    }

    init {
        onEvent(UpdateAvatarEvent.OnGenerateAvatar)
    }

    private fun generateAvatars() {
        _generatedAvatarsState.update { List(3) { UiState.Loading() } }
        _selectedAvatarUrl.update { null }
        _uploadAvatarState.update { UiState.Idle() }

        repeat(3) { index ->
            viewModelScope.launch {
                updateAvatarRepository.generateAvatar().collect { state ->
                    _generatedAvatarsState.update { currentList ->
                        currentList.toMutableList().also { it[index] = state }
                    }
                }
            }
        }
    }

    private fun selectGeneratedAvatar(avatarUrl: String) {
        _selectedAvatarUrl.update { avatarUrl }
        _locallySelectedBase64.update { null }
        _uploadAvatarState.update { UiState.Idle() }
    }

    private fun updateUserAvatar(avatarUrl: String) {
        viewModelScope.launch {
            updateAvatarRepository.updateUserAvatar(avatarUrl).collect { state->
                _updateAvatarState.update { state }
            }
        }
    }

    private fun uploadImage(avatarBase64: String) {
        _locallySelectedBase64.update { avatarBase64 }
        _selectedAvatarUrl.update { null }
        _uploadAvatarState.update { UiState.Loading() }

        val uploadImageRequest = UploadImageRequest(image = avatarBase64, name = null, expiration = null)
        viewModelScope.launch {
            updateAvatarRepository.uploadImage(uploadImageRequest).collect { state->
                _uploadAvatarState.update { state }
                if (state is UiState.Success) {
                    _selectedAvatarUrl.update { state.data.data.display_url }
                    _locallySelectedBase64.update { null }
                } else if (state is UiState.Error) {
                    _locallySelectedBase64.update { null }
                }
            }
        }
    }
}