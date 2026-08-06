package yosel.dev.atti.core.models.model

data class ClientWithPatientsModel(
    val client: ClientModel = ClientModel(),
    val patients: List<PatientModel> = emptyList()
)
