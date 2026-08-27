package yosel.dev.atti.screens.anamnesis_form.ui

import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisDewormingWithDetailsModel
import yosel.dev.atti.core.models.model.AnamnesisEnvironmentOptionModel
import yosel.dev.atti.core.models.model.AnamnesisModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineWithDetailsModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.parseToDouble

data class AnamnesisFormInputsState(
    val hasOutdoorAccess: Boolean = false,
    val selectedEnvironmentOptions: List<AppCatalogModel> = emptyList(),
    val vaccines: List<AnamnesisVaccineWithDetailsModel> = emptyList(),
    val dewormings: List<AnamnesisDewormingWithDetailsModel> = emptyList(),
    val housemates: String = "",
    val selectedFoodBrand: AppCatalogModel? = null,
    val selectedFoodUnit: AppCatalogModel? = null,
    val foodQuantity: String = "",
    val hasHomemadeFood: Boolean = false,
    val homemadeFoodDetails: String = "",
    val feedingFrequency: String = "2 veces al día",
    val waterConsumption: String = "Normal"
) {
    fun toAnamnesisModel(consultationId: String = "") = AnamnesisModel(
        consultationId = consultationId,
        hasOutdoorAccess = hasOutdoorAccess,
        housemates = housemates.trim(),
        foodBrandId = selectedFoodBrand?.id,
        foodQuantity = foodQuantity.parseToDouble(),
        foodUnitTypeId = selectedFoodUnit?.id,
        homemadeFood = if (hasHomemadeFood) homemadeFoodDetails.trim() else "No",
        feedingFrequency = feedingFrequency,
        waterConsumption = waterConsumption,
        status = Constants.ACTIVE_STATUS
    )

    fun toEnvironmentOptionModels(anamnesisId: String = ""): List<AnamnesisEnvironmentOptionModel> {
        return selectedEnvironmentOptions.map { catalog ->
            AnamnesisEnvironmentOptionModel(
                anamnesisId = anamnesisId,
                catalogId = catalog.id
            )
        }
    }

    fun toVaccineModels(anamnesisId: String = ""): List<AnamnesisVaccineModel> {
        return vaccines.map { it.vaccineEntry.copy(anamnesisId = anamnesisId) }
    }

    fun toDewormingModels(anamnesisId: String = ""): List<AnamnesisDewormingModel> {
        return dewormings.map { it.deworming.copy(anamnesisId = anamnesisId) }
    }

    fun hasChangesFrom(initial: AnamnesisFormInputsState): Boolean {
        return hasOutdoorAccess != initial.hasOutdoorAccess ||
                selectedEnvironmentOptions != initial.selectedEnvironmentOptions ||
                vaccines != initial.vaccines ||
                dewormings != initial.dewormings ||
                housemates.trim() != initial.housemates.trim() ||
                selectedFoodBrand?.id != initial.selectedFoodBrand?.id ||
                selectedFoodUnit?.id != initial.selectedFoodUnit?.id ||
                foodQuantity != initial.foodQuantity ||
                hasHomemadeFood != initial.hasHomemadeFood ||
                homemadeFoodDetails.trim() != initial.homemadeFoodDetails.trim() ||
                feedingFrequency != initial.feedingFrequency ||
                waterConsumption != initial.waterConsumption
    }
}
