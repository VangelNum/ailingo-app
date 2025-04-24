package org.ailingo.app.features.profileupdate.presentation

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@JsName("jsSelectImage")
private external fun jsSelectImage(callback: (String?) -> Unit)

actual suspend fun selectImage(): String? = suspendCoroutine { continuation ->
    jsSelectImage { result ->
        continuation.resume(result)
    }
}