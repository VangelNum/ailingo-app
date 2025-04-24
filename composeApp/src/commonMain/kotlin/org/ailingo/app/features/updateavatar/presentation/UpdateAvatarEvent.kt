package org.ailingo.app.features.updateavatar.presentation

sealed class UpdateAvatarEvent {
    data class OnUploadImage(val base64Image: String) : UpdateAvatarEvent()
    data object OnUpdateUserAvatar : UpdateAvatarEvent()
    data object OnGenerateAvatar : UpdateAvatarEvent()
    data class OnSelectGeneratedAvatar(val avatarUrl: String) : UpdateAvatarEvent()
}