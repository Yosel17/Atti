package yosel.dev.atti.core.models.model

data class ClientWithPatientsWithDetailsModel(
    val client: ClientModel = ClientModel(),
    val patients: List<PatientWithDetailsModel> = emptyList()
){
    val sortedPatients: List<PatientWithDetailsModel>
        get() = patients.sortedByDescending { it.patient.createdAt }
}
