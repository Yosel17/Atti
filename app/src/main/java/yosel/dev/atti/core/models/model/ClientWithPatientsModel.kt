package yosel.dev.atti.core.models.model

data class ClientWithPatientsModel(
    val client: ClientModel = ClientModel(),
    val patients: List<PatientModel> = emptyList()
){
    val sortedPatients: List<PatientModel>
        get() = patients.sortedByDescending { it.createdAt }
}
