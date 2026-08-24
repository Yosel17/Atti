package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class AnamnesisVaccineModel(
    val id: Int = 0,
    val anamnesisId: String = "",
    val applicationDate: String = "",
    val vaccineCatalogId: Int = 0,
    val schemeCatalogId: Int = 0,
    val createdAt: String = ""
) {
    val formattedApplicationDate: String
        get() = formatDate(isoString = applicationDate)
}
