package yosel.dev.atti.core.models.model

data class ClientWithPatientsWithCatalogsModel(
    val client: ClientModel = ClientModel(),
    val patients: List<PatientWithCatalogsModel> = emptyList()
){
    val sortedPatients: List<PatientWithCatalogsModel>
        get() = patients.sortedByDescending { it.patient.createdAt }
}
