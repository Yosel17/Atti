package yosel.dev.atti.screens.follow_up_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import java.time.LocalDate
import java.time.LocalTime

sealed interface FollowUpFormAction {
    data object TryLoadAgain : FollowUpFormAction
    data object SaveFollowUp : FollowUpFormAction
    data class ToggleSaveDialog(val show: Boolean) : FollowUpFormAction
    data class ToggleDatePickerDialog(val show: Boolean) : FollowUpFormAction
    data class OnSelectDate(val date: LocalDate) : FollowUpFormAction
    data class OnSelectDateForCalendar(val date: LocalDate) : FollowUpFormAction
    data class OnSelectTime(val time: LocalTime) : FollowUpFormAction
    data object OnResetToDaySelector : FollowUpFormAction
    data class OnReasonChange(val reason: String) : FollowUpFormAction

    // BottomSheet Motivos Rápidos (Catálogo 20)
    data object OnOpenQuickReasonSheet : FollowUpFormAction
    data object OnDismissQuickReasonSheet : FollowUpFormAction
    data class OnQuickReasonSearchQueryChange(val query: String) : FollowUpFormAction
    data class OnSelectQuickReason(val catalog: AppCatalogModel) : FollowUpFormAction
    data object OnShowAddQuickReasonDialog : FollowUpFormAction
    data object OnDismissAddQuickReasonDialog : FollowUpFormAction
    data class OnSaveQuickReasonCatalog(val name: String) : FollowUpFormAction
}