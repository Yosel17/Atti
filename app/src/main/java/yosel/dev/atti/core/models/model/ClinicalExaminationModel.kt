package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ClinicalExaminationModel(
    val id: String = "",
    val consultationId: String = "",
    val mucousMembranes: String = "",
    val coatCatalogId: Int? = null,
    val abdominalPalpation: String = "",
    val bodyCondition: Int = 3,
    val otherFindings: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
