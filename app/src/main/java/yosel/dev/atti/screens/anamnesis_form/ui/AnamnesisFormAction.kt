package yosel.dev.atti.screens.anamnesis_form.ui

import yosel.dev.atti.core.models.model.AnamnesisDewormingWithDetailsModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineWithDetailsModel
import yosel.dev.atti.core.models.model.AppCatalogModel

sealed interface AnamnesisFormAction {

    data object TryCatalogsAgain: AnamnesisFormAction
    data object SaveAnamnesis : AnamnesisFormAction

    // Entorno y rutina
    data class OnOutdoorAccessChange(val enabled: Boolean) : AnamnesisFormAction
    data object OnOpenEnvironmentOptionsSheet : AnamnesisFormAction
    data object OnDismissEnvironmentOptionsSheet : AnamnesisFormAction
    data class OnSearchEnvironmentQueryChange(val query: String) : AnamnesisFormAction
    data class OnToggleEnvironmentOption(val catalog: AppCatalogModel) : AnamnesisFormAction
    data class OnRemoveEnvironmentOption(val catalog: AppCatalogModel) : AnamnesisFormAction

    // Profilaxis - Vacunas
    data object OnOpenAddVaccineSheet : AnamnesisFormAction
    data object OnDismissAddVaccineSheet : AnamnesisFormAction
    data class OnVaccineDateChange(val dateIso: String, val dateDisplay: String, val elapsedText: String) : AnamnesisFormAction
    data object OnOpenVaccineNameSheet : AnamnesisFormAction
    data object OnDismissVaccineNameSheet : AnamnesisFormAction
    data class OnSearchVaccineNameQueryChange(val query: String) : AnamnesisFormAction
    data class OnSelectVaccineName(val vaccine: AppCatalogModel) : AnamnesisFormAction
    data class OnSelectVaccineSchedule(val schedule: AppCatalogModel) : AnamnesisFormAction
    data object OnSaveVaccineEntry : AnamnesisFormAction
    data class OnDeleteVaccine(val vaccine: AnamnesisVaccineWithDetailsModel) : AnamnesisFormAction

    // Profilaxis - Desparasitantes
    data object OnOpenAddDewormingSheet : AnamnesisFormAction
    data object OnDismissAddDewormingSheet : AnamnesisFormAction
    data class OnDewormingDateChange(val dateIso: String, val dateDisplay: String, val elapsedText: String) : AnamnesisFormAction
    data class OnDewormingTypeChange(val type: String) : AnamnesisFormAction
    data object OnOpenDewormingProductSheet : AnamnesisFormAction
    data object OnDismissDewormingProductSheet : AnamnesisFormAction
    data class OnSearchDewormingProductQueryChange(val query: String) : AnamnesisFormAction
    data class OnSelectDewormingProduct(val product: AppCatalogModel) : AnamnesisFormAction
    data object OnSaveDewormingEntry : AnamnesisFormAction
    data class OnDeleteDeworming(val deworming: AnamnesisDewormingWithDetailsModel) : AnamnesisFormAction

    // Compañeros
    data class OnHousematesChange(val value: String) : AnamnesisFormAction

    // Alimentación
    data object OnOpenConcentrateBrandSheet : AnamnesisFormAction
    data object OnDismissConcentrateBrandSheet : AnamnesisFormAction
    data class OnSearchConcentrateBrandQueryChange(val query: String) : AnamnesisFormAction
    data class OnSelectConcentrateBrand(val brand: AppCatalogModel) : AnamnesisFormAction

    data object OnOpenConcentrateUnitSheet : AnamnesisFormAction
    data object OnDismissConcentrateUnitSheet : AnamnesisFormAction
    data class OnSearchConcentrateUnitQueryChange(val query: String) : AnamnesisFormAction
    data class OnSelectConcentrateUnit(val unit: AppCatalogModel) : AnamnesisFormAction

    data class OnFoodQuantityChange(val quantity: String) : AnamnesisFormAction
    data class OnHomemadeFoodToggle(val enabled: Boolean) : AnamnesisFormAction
    data class OnHomemadeFoodDetailsChange(val details: String) : AnamnesisFormAction
    data class OnFeedingFrequencyChange(val frequency: String) : AnamnesisFormAction
    data class OnWaterConsumptionChange(val consumption: String) : AnamnesisFormAction

    // Creación de catálogos generales
    data class OnShowAddCatalogDialog(val catalogTypeId: Int, val catalogTypeName: String) : AnamnesisFormAction
    data object OnDismissAddCatalogDialog : AnamnesisFormAction
    data class OnSaveAppCatalog(val name: String) : AnamnesisFormAction
}