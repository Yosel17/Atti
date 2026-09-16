package yosel.dev.atti.screens.anamnesis_form.ui

import yosel.dev.atti.core.models.model.AnamnesisModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.utils.Constants

data class AnamnesisFormState(
    val isEditMode: Boolean = false,
    val anamnesisId: String? = null,
    val currentAnamnesis: AnamnesisModel? = null,
    val initialFormInputState: AnamnesisFormInputsState = AnamnesisFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,
    val isLoadingSaveAnamnesis: Boolean = false,
    val isLoadingUpdateAnamnesis: Boolean = false,
    val formInputState: AnamnesisFormInputsState = AnamnesisFormInputsState(),
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),

    // Catálogos base
    val animalLifestyles: List<AppCatalogModel> = emptyList(),
    val filteredAnimalLifestyles: List<AppCatalogModel> = emptyList(),
    val lifestyleSearchQuery: String = "",
    val isLifestyleSheetOpen: Boolean = false,

    val vaccineNames: List<AppCatalogModel> = emptyList(),
    val filteredVaccineNames: List<AppCatalogModel> = emptyList(),
    val vaccineNameSearchQuery: String = "",
    val isVaccineNameSheetOpen: Boolean = false,

    val vaccinationSchedules: List<AppCatalogModel> = emptyList(),

    val internalDewormers: List<AppCatalogModel> = emptyList(),
    val externalDewormers: List<AppCatalogModel> = emptyList(),
    val filteredDewormerProducts: List<AppCatalogModel> = emptyList(),
    val dewormerProductSearchQuery: String = "",
    val isDewormerProductSheetOpen: Boolean = false,

    val concentrateBrands: List<AppCatalogModel> = emptyList(),
    val filteredConcentrateBrands: List<AppCatalogModel> = emptyList(),
    val concentrateBrandSearchQuery: String = "",
    val isConcentrateBrandSheetOpen: Boolean = false,

    val concentrateUnitsOfMeasurement: List<AppCatalogModel> = emptyList(),
    val filteredConcentrateUnits: List<AppCatalogModel> = emptyList(),
    val concentrateUnitSearchQuery: String = "",
    val isConcentrateUnitSheetOpen: Boolean = false,

    // Arenero (Felino)
    val litterBrands: List<AppCatalogModel> = emptyList(),
    val filteredLitterBrands: List<AppCatalogModel> = emptyList(),
    val litterBrandSearchQuery: String = "",
    val isLitterBrandSheetOpen: Boolean = false,

    val litterUnitsOfMeasurement: List<AppCatalogModel> = emptyList(),
    val filteredLitterUnits: List<AppCatalogModel> = emptyList(),
    val litterUnitSearchQuery: String = "",
    val isLitterUnitSheetOpen: Boolean = false,

    // Sheets de creación de vacunas y desparasitantes
    val isAddVaccineSheetOpen: Boolean = false,
    val tempVaccineIsoDate: String = "",
    val tempVaccineDisplayDate: String = "",
    val tempVaccineElapsedText: String = "",
    val tempSelectedVaccineCatalog: AppCatalogModel? = null,
    val tempSelectedScheduleCatalog: AppCatalogModel? = null,

    val isAddDewormingSheetOpen: Boolean = false,
    val tempDewormingIsoDate: String = "",
    val tempDewormingDisplayDate: String = "",
    val tempDewormingElapsedText: String = "",
    val tempDewormingType: String = "INTERNO",
    val tempSelectedDewormerProduct: AppCatalogModel? = null,

    // Diálogo general para agregar catálogo
    val showAddAppCatalogDialog: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = "",
    val isLoadingAddCatalog: Boolean = false
) {
    val isFeline: Boolean
        get() = consultationWithDetails.patientWithDetails.patient.speciesId == Constants.FELINE_SPECIES_CATALOG ||
                consultationWithDetails.patientWithDetails.species.id == Constants.FELINE_SPECIES_CATALOG
}
