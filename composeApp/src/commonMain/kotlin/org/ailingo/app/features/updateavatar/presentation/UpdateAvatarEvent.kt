package org.ailingo.app.features.updateavatar.presentation

sealed class UpdateAvatarEvent {
    data class OnUploadImage(val base64Image: String) : UpdateAvatarEvent()
    data class OnUpdateUserAvatar(val imageUrl: String) : UpdateAvatarEvent()
    data object OnGenerateAvatar : UpdateAvatarEvent()
}