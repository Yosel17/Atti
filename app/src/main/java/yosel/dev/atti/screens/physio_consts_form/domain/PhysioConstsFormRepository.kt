package yosel.dev.atti.screens.physio_consts_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PhysiologicalConstsModel
import yosel.dev.atti.core.models.model.PhysiologicalConstsWithDetailsModel

interface PhysioConstsFormRepository {

    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun savePhysiologicalConsts(constants: PhysiologicalConstsModel): Result<PhysiologicalConstsModel>
    suspend fun updatePhysiologicalConsts(constants: PhysiologicalConstsModel): Result<PhysiologicalConstsModel>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getPhysiologicalConstsWithDetailsById(id: String): Result<PhysiologicalConstsWithDetailsModel>
}