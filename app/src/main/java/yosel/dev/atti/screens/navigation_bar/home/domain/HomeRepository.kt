package yosel.dev.atti.screens.navigation_bar.home.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import java.time.YearMonth

interface HomeRepository {
    fun getFollowUpsForMonthFlow(yearMonth: YearMonth): Flow<List<FollowUpWithDetailsModel>>
    suspend fun syncFollowUpsForMonth(yearMonth: YearMonth): Result<Unit>
    fun getLowStockProductsFlow(): Flow<List<ProductWithDetailsModel>>
    suspend fun syncLowStockProducts(): Result<Unit>
}