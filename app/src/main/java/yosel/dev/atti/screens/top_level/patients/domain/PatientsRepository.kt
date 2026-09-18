package yosel.dev.atti.screens.top_level.patients.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.PatientWithDetailsModel

interface PatientsRepository {
    fun getAllPatientsWithCatalogs(): Flow<List<PatientWithDetailsModel>>
    suspend fun syncPatients(): Result<Unit>
}