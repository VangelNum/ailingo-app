package org.ailingo.app.features.profileupdate.presentation

import android.net.Uri
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import org.ailingo.app.AndroidApp

object ImagePickerActivityResult {
    private var launcher: ActivityResultLauncher<String>? = null
    private var deferredResult: CompletableDeferred<Uri?>? = null

    fun init(registry: ActivityResultRegistry, lifecycleOwner: LifecycleOwner) {
        launcher = registry.register(
            "image_picker_key_ailingo",
            lifecycleOwner,
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            deferredResult?.complete(uri)
            deferredResult = null
        }
    }

    fun release() {
        launcher?.unregister()
        launcher = null
        deferredResult?.cancel()
        deferredResult = null
    }

    suspend fun pickImageUri(): Uri? {
        if (launcher == null) {
            error("ImagePickerActivityResult is not initialized. Call init() from your Activity.")
        }

        deferredResult = CompletableDeferred()
        launcher?.launch("image/*")
        return deferredResult?.await()
    }
}

actual suspend fun selectImage(): String? {
    val uri = ImagePickerActivityResult.pickImageUri()
    if (uri == null) {
        return null
    }

    val context = AndroidApp.INSTANCE.applicationContext
    val contentResolver = context.contentResolver

    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}