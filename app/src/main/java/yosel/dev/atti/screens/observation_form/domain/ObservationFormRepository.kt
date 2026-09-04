package yosel.dev.atti.screens.observation_form.domain

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ObservationModel

interface ObservationFormRepository {
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getObservationByConsultationId(consultationId: String): Result<ObservationModel?>
    suspend fun saveObservation(observation: ObservationModel): Result<ObservationModel>
    suspend fun updateObservation(observation: ObservationModel): Result<ObservationModel>
}