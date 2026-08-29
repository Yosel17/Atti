package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class PhysiologicalConstsModel(
    val id: String = "",
    val consultationId: String = "",
    val temperature: Double? = null,
    val heartRate: Int? = null,
    val respiratoryRate: Int? = null,
    val weight: Double? = null,
    val weightUnitCatalogId: Int? = null,
    val capillaryRefillTime: Int? = null,
    val skinTurgor: Int? = null,
    val createdAt: String = "",
    val status: Int = 1
){
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
