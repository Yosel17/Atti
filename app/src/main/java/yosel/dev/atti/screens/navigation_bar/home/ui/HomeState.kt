package yosel.dev.atti.screens.navigation_bar.home.ui

import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import java.time.LocalDate
import java.time.YearMonth

data class HomeState(
    val currentYearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoadingMonthAppointments: Boolean = false,
    val monthFollowUps: List<FollowUpWithDetailsModel> = emptyList(),
    val selectedDayFollowUps: List<FollowUpWithDetailsModel> = emptyList(),
    val isLoadingLowStock: Boolean = false,
    val lowStockProducts: List<ProductWithDetailsModel> = emptyList()
)
