package yosel.dev.atti.screens.auxiliary_test_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.AuxiliaryTestWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface AuxiliaryTestFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveAuxiliaryTests(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AuxiliaryTestWithDetailsModel>>
    suspend fun updateAuxiliaryTests(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AuxiliaryTestWithDetailsModel>>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getAuxiliaryTestsByConsultationId(consultationId: String): Result<List<AuxiliaryTestWithDetailsModel>>
}
