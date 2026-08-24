package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class AnamnesisDewormingModel(
    val id: Int = 0,
    val anamnesisId: String = "",
    val applicationDate: String = "",
    val dewormingType: String = "",
    val productCatalogId: Int = 0,
    val createdAt: String = ""
) {
    val formattedApplicationDate: String
        get() = formatDate(isoString = applicationDate)
}
