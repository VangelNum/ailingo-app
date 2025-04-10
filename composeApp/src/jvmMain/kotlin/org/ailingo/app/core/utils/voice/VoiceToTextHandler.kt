package org.ailingo.app.core.utils.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class VoiceToTextHandler {
    actual val state: StateFlow<VoiceToTextState> = MutableStateFlow(VoiceToTextState.Idle)
    actual var isAvailable: Boolean
        get() = false
        set(value) { }

    actual fun startListening() {
        // TODO: Implement voice recognition on desktop
        Logger.i("VoiceToTextHandler.startListening() - Desktop Stub - No operation")
    }

    actual fun stopListening() {
        // TODO: Implement voice recognition on desktop
        Logger.i("VoiceToTextHandler.stopListening() - Desktop Stub - No operation")
    }
}

@Composable
actual fun rememberVoiceToTextHandler(): VoiceToTextHandler {
    return remember { VoiceToTextHandler() }
}