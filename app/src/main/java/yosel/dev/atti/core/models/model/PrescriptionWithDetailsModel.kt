package yosel.dev.atti.core.models.model

data class PrescriptionWithDetailsModel(
    val prescription: PrescriptionModel = PrescriptionModel(),
    val items: List<PrescriptionItemWithDetailsModel> = emptyList()
)
