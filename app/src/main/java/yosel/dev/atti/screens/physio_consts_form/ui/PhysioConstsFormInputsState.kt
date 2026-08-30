package yosel.dev.atti.screens.physio_consts_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PhysiologicalConstsModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.parseToDouble
import yosel.dev.atti.core.utils.parseToInt

data class PhysioConstsFormInputsState(
    val temperature: String = "",
    val heartRate: String = "",
    val respiratoryRate: String = "",
    val weight: String = "",
    val selectedWeightUnit: AppCatalogModel? = null,
    val capillaryRefillTime: Int = 2,
    val skinTurgor: Int = 1
) {
    fun toModel(consultationId: String = "") = PhysiologicalConstsModel(
        consultationId = consultationId,
        temperature = if (temperature.isNotBlank()) temperature.parseToDouble() else null,
        heartRate = if (heartRate.isNotBlank()) heartRate.parseToInt() else null,
        respiratoryRate = if (respiratoryRate.isNotBlank()) respiratoryRate.parseToInt() else null,
        weight = if (weight.isNotBlank()) weight.parseToDouble() else null,
        weightUnitCatalogId = selectedWeightUnit?.id,
        capillaryRefillTime = capillaryRefillTime,
        skinTurgor = skinTurgor,
        status = Constants.ACTIVE_STATUS
    )

    fun toUpdateModel(
        constsId: String,
        consultationId: String,
        createdAt: String = "",
        status: Int = Constants.ACTIVE_STATUS
    ) = PhysiologicalConstsModel(
        id = constsId,
        consultationId = consultationId,
        temperature = if (temperature.isNotBlank()) temperature.parseToDouble() else null,
        heartRate = if (heartRate.isNotBlank()) heartRate.parseToInt() else null,
        respiratoryRate = if (respiratoryRate.isNotBlank()) respiratoryRate.parseToInt() else null,
        weight = if (weight.isNotBlank()) weight.parseToDouble() else null,
        weightUnitCatalogId = selectedWeightUnit?.id,
        capillaryRefillTime = capillaryRefillTime,
        skinTurgor = skinTurgor,
        createdAt = createdAt,
        status = status
    )

    fun hasChangesFrom(initial: PhysioConstsFormInputsState): Boolean {
        return temperature.trim() != initial.temperature.trim() ||
                heartRate.trim() != initial.heartRate.trim() ||
                respiratoryRate.trim() != initial.respiratoryRate.trim() ||
                weight.trim() != initial.weight.trim() ||
                selectedWeightUnit?.id != initial.selectedWeightUnit?.id ||
                capillaryRefillTime != initial.capillaryRefillTime ||
                skinTurgor != initial.skinTurgor
    }
}