package yosel.dev.atti.screens.detail_patient.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel

interface DetailPatientRepository {

    fun getPatientByIdFlow(patientId: String): Flow<Result<PatientModel>>

    suspend fun getClientByIdRoom(clientId: String): Result<ClientModel>

    suspend fun getClientByIdSupabase(clientId: String): Result<ClientModel>

    suspend fun changeStatusPatient(patientId: String, newStatus: Int): Result<Unit>
}