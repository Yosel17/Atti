package yosel.dev.atti.screens.top_level.home.ui

import java.time.LocalDate

sealed interface HomeAction {
    data class OnSelectDate(val date: LocalDate) : HomeAction
    data object OnPreviousMonth : HomeAction
    data object OnNextMonth : HomeAction
    data class OnAppointmentClick(val consultationId: String, val consultationTypeId: Int) : HomeAction
    data class OnProductClick(val productId: String) : HomeAction
    data object OnRetryMonthFollowUps : HomeAction
    data object OnRetryLowStock : HomeAction
}