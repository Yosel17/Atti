package yosel.dev.atti.screens.consent_form.ui

import android.net.Uri

data class ConsentFormInputsState(
    val imageUri: Uri? = null,
){
    val isValid: Boolean
        get() = imageUri != null && imageUri.toString().isNotBlank()

    fun hasChangesFrom(initial: ConsentFormInputsState): Boolean {
        return imageUri != initial.imageUri
    }
}
