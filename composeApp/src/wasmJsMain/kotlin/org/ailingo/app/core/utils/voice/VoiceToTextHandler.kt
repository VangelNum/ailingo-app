package org.ailingo.app.core.utils.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@JsName("setRecognitionCallback")
external fun setJsRecognitionCallback(callback: (String) -> Unit)

@JsName("setListeningCallback")
external fun setJsListeningCallback(callback: (Boolean) -> Unit)

@JsName("setMicrophoneErrorCallback")
external fun setJsMicrophoneErrorCallback(callback: (String) -> Unit)

@JsName("startListening")
external fun jsStartListening()

@JsName("stopListening")
external fun jsStopListening()

actual class VoiceToTextHandler {
    private val _state = MutableStateFlow<VoiceToTextState>(VoiceToTextState.Idle)
    actual val state: StateFlow<VoiceToTextState> = _state.asStateFlow()
    actual var isAvailable: Boolean = true

    init {
        if (isAvailable) {
            setJsRecognitionCallback { result ->
                _state.value = VoiceToTextState.Result(result)
            }
            setJsListeningCallback { isListening ->
                _state.value = if (isListening) VoiceToTextState.Listening else VoiceToTextState.Idle
            }
            setJsMicrophoneErrorCallback { errorMessage ->
                _state.value = VoiceToTextState.Error(errorMessage)
            }
        } else {
            _state.value = VoiceToTextState.Error("Speech recognition is not available in this browser.")
        }
    }

    actual fun startListening() {
        if (!isAvailable) {
            _state.value = VoiceToTextState.Error("Speech recognition is not available.")
            return
        }
        _state.value = VoiceToTextState.Listening
        jsStartListening()
    }


    actual fun stopListening() {
        if (!isAvailable) return
        jsStopListening()
        _state.value = VoiceToTextState.Idle
    }
}

@Composable
actual fun rememberVoiceToTextHandler(): VoiceToTextHandler {
    return remember { VoiceToTextHandler() }
}