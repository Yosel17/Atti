package yosel.dev.atti.screens.add_patient.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel

interface AddPatientRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun insertPatient(patient: PatientModel): Result<Unit>
    suspend fun updatePatient(patient: PatientModel): Result<Unit>
    suspend fun getPatientByIdRoom(patientId: String): Result<PatientModel>
    suspend fun getClientByIdRoom(clientId: String): Result<ClientModel>
    suspend fun getClientByIdSupabase(clientId: String): Result<ClientModel>
    suspend fun getClients(): Result<List<ClientModel>>
}