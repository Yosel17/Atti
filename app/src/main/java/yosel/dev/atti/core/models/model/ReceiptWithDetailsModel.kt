package yosel.dev.atti.core.models.model

data class ReceiptWithDetailsModel(
    val receipt: ReceiptModel = ReceiptModel(),
    val consultationWithDetails: ConsultationWithDetailsModel? = null,
    val items: List<ReceiptItemWithDetailsModel> = emptyList()
) {
    val clientOrCustomerName: String
        get() = when {
            receipt.customerName.isNotBlank() -> receipt.customerName
            consultationWithDetails != null -> {
                val patientName = consultationWithDetails.patientWithDetails.patient.name
                "Consulta: $patientName"
            }
            else -> "Cliente General"
        }
}
