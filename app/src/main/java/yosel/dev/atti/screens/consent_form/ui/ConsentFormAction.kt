package yosel.dev.atti.screens.consent_form.ui

import android.net.Uri

sealed interface ConsentFormAction {

    data object TryLoadAgain : ConsentFormAction
    data object SaveConsent : ConsentFormAction
    data class ToggleSaveDialog(val show: Boolean) : ConsentFormAction
    data object OnDismissBottomSheet : ConsentFormAction
    data object OnSelectCameraClick : ConsentFormAction
    data object OnSelectGalleryClick : ConsentFormAction
    data class OnImageSelected(val uri: Uri?) : ConsentFormAction
    data object OnShowDialogConfirmation : ConsentFormAction
    data object OnObtainPermits : ConsentFormAction
    data class OnToggleRationaleDialog(val show: Boolean) : ConsentFormAction
    data class OnToggleSettingsDialog(val show: Boolean) : ConsentFormAction
    data object OnUploadImageClick : ConsentFormAction
}