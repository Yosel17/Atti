package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class AnamnesisModel(
    val id: String = "",
    val consultationId: String = "",
    val hasOutdoorAccess: Boolean = false,
    val housemates: String = "",
    val foodBrandId: Int = 0,
    val foodQuantity: Double = 0.0,
    val foodUnitTypeId: Int = 0,
    val homemadeFood: String = "",
    val feedingFrequency: String = "",
    val waterConsumption: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
