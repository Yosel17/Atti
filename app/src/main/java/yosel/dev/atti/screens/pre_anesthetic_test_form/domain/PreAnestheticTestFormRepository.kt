package yosel.dev.atti.screens.pre_anesthetic_test_form.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PreAnestheticTestModel
import yosel.dev.atti.core.models.model.PreAnestheticTestWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

interface PreAnestheticTestFormRepository {
    fun getActiveProductsWithDetailsFlow(): Flow<List<ProductWithDetailsModel>>
    fun getActiveServicesWithDetailsFlow(): Flow<List<ServiceWithDetailsModel>>
    suspend fun syncProducts(): Result<Unit>
    suspend fun syncServices(): Result<Unit>
    suspend fun savePreAnestheticTests(
        consultationId: String,
        tests: List<PreAnestheticTestModel>
    ): Result<List<PreAnestheticTestWithDetailsModel>>
    suspend fun updatePreAnestheticTests(
        consultationId: String,
        tests: List<PreAnestheticTestModel>
    ): Result<List<PreAnestheticTestWithDetailsModel>>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getPreAnestheticTestsByConsultationId(consultationId: String): Result<List<PreAnestheticTestWithDetailsModel>>
}
