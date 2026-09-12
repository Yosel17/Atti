package yosel.dev.atti.screens.consent_form.ui

import android.net.Uri

data class ConsentFormInputsState(
    val imageUri: Uri? = null,
    val imageUrl: String? = null,
){
    val displayImage: Any?
        get() = imageUri ?: imageUrl.takeIf { !it.isNullOrBlank() }

    val isValid: Boolean
        get() = displayImage != null

    fun hasChangesFrom(initial: ConsentFormInputsState): Boolean {
        return (imageUri != initial.imageUri) || (imageUrl != initial.imageUrl)
    }
}
