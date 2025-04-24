package org.ailingo.app

import co.touchlab.kermit.Logger
import kotlinx.browser.document
import kotlinx.browser.window
import org.ailingo.app.core.utils.deviceinfo.util.PlatformName
import org.w3c.dom.HTMLAudioElement

internal actual fun openUrl(url: String?) {
    url?.let { window.open(it) }
}

actual fun getPlatformName(): PlatformName {
    return PlatformName.Web
}

actual fun playSound(sound: String) {
    val audioElement = document.createElement("audio") as HTMLAudioElement
    audioElement.src = sound
    audioElement.controls = false
    audioElement.onended = {
        document.body?.removeChild(audioElement)
    }
    try {
        document.body?.appendChild(audioElement)
        audioElement.play().catch { error ->
            Logger.e("Ошибка при попытке воспроизвести звук '$sound': $error")
            document.body?.removeChild(audioElement)
        }
    } catch (e: Throwable) {
        Logger.e("Синхронная ошибка при создании или добавлении audio элемента:", e)
    }
}