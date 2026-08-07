package yosel.dev.atti.screens.detail_client.ui

sealed interface DetailClientAction {
    data class OnCallClick(val phoneNumber: String) : DetailClientAction
    data class OnWhatsappClick(val phoneNumber: String) : DetailClientAction
    data object OnEditClick : DetailClientAction
    data object OnDismissEdit : DetailClientAction
    data object OnUpdateClient : DetailClientAction
    data class OnChangeEditFormValue(val value: String, val field: Int) : DetailClientAction
}