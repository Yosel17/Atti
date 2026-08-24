package yosel.dev.atti.screens.anamnesis_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class AnamnesisFormState(
    val isLoadingDataInitial: Boolean = true,
    val animalLifestyles: List<AppCatalogModel> = emptyList(),
    val vaccineNames: List<AppCatalogModel> = emptyList(),
    val vaccinationSchedules: List<AppCatalogModel> = emptyList(),
    val internalDewormers: List<AppCatalogModel> = emptyList(),
    val externalDewormers: List<AppCatalogModel> = emptyList(),
    val concentrateBrands: List<AppCatalogModel> = emptyList(),
    val concentrateUnitsOfMeasurement: List<AppCatalogModel> = emptyList(),
)
