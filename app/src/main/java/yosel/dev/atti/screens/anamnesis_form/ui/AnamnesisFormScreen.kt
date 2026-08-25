package yosel.dev.atti.screens.anamnesis_form.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AddAppCatalogDialog
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.SelectAppCatalogBottomSheet
import yosel.dev.atti.core.components.SelectAppCatalogMultiBottomSheet
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnamnesisFormScreen(
    modifier: Modifier = Modifier,
    state: AnamnesisFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (AnamnesisFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Anamnesis",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
        ) {
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when {
                        targetState.isLoadingDataInitial -> "LOADING"
                        !targetState.isSuccessGetCatalogs -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "AnamnesisScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    !targetState.isSuccessGetCatalogs -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la información inicial",
                            subTitle = "No es posible registrar la anamnesis sin esa información. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(AnamnesisFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        BodyAnamnesisForm(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                            onAction = onAction
                        )
                    }
                }
            }
        }

        // Sheet: Entorno y rutina (Multiselección)
        if (state.isLifestyleSheetOpen) {
            SelectAppCatalogMultiBottomSheet(
                onDismiss = { onAction(AnamnesisFormAction.OnDismissEnvironmentOptionsSheet) },
                title = "Opciones de entorno",
                search = state.lifestyleSearchQuery,
                onSearchChange = { onAction(AnamnesisFormAction.OnSearchEnvironmentQueryChange(it)) },
                filteredAppCatalogs = state.filteredAnimalLifestyles,
                selectedAppCatalogs = state.formInputState.selectedEnvironmentOptions,
                onToggleAppCatalog = { onAction(AnamnesisFormAction.OnToggleEnvironmentOption(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        AnamnesisFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.ANIMAL_LIFESTYLE_TYPE_CATALOG,
                            catalogTypeName = "Opción de entorno"
                        )
                    )
                },
                catalogosEmpty = state.animalLifestyles.isEmpty()
            )
        }

        // Sheet: Agregar Vacuna (Modal completo)
        if (state.isAddVaccineSheetOpen) {
            AddVaccineBottomSheet(
                state = state,
                onDismiss = { onAction(AnamnesisFormAction.OnDismissAddVaccineSheet) },
                onAction = onAction
            )
        }

        // Sheet: Seleccionar nombre de vacuna
        if (state.isVaccineNameSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(AnamnesisFormAction.OnDismissVaccineNameSheet) },
                title = "Selecciona una vacuna",
                search = state.vaccineNameSearchQuery,
                onSearchChange = { onAction(AnamnesisFormAction.OnSearchVaccineNameQueryChange(it)) },
                filteredAppCatalogs = state.filteredVaccineNames,
                selectedAppCatalog = state.tempSelectedVaccineCatalog,
                onSelectAppCatalog = { onAction(AnamnesisFormAction.OnSelectVaccineName(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        AnamnesisFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.VACCINE_NAME_TYPE_CATALOG,
                            catalogTypeName = "Vacuna"
                        )
                    )
                },
                catalogosEmpty = state.vaccineNames.isEmpty()
            )
        }

        // Sheet: Agregar Desparasitante (Modal completo)
        if (state.isAddDewormingSheetOpen) {
            AddDewormingBottomSheet(
                state = state,
                onDismiss = { onAction(AnamnesisFormAction.OnDismissAddDewormingSheet) },
                onAction = onAction
            )
        }

        // Sheet: Seleccionar producto desparasitante
        if (state.isDewormerProductSheetOpen) {
            val catalogType = if (state.tempDewormingType == "Interno") Constants.INTERNAL_DEWORMER_TYPE_CATALOG else Constants.EXTERNAL_DEWORMER_TYPE_CATALOG
            val catalogName = if (state.tempDewormingType == "Interno") "Desparasitante interno" else "Desparasitante externo"
            val baseList = if (state.tempDewormingType == "Interno") state.internalDewormers else state.externalDewormers

            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(AnamnesisFormAction.OnDismissDewormingProductSheet) },
                title = "Selecciona desparasitante (${state.tempDewormingType.lowercase()})",
                search = state.dewormerProductSearchQuery,
                onSearchChange = { onAction(AnamnesisFormAction.OnSearchDewormingProductQueryChange(it)) },
                filteredAppCatalogs = state.filteredDewormerProducts,
                selectedAppCatalog = state.tempSelectedDewormerProduct,
                onSelectAppCatalog = { onAction(AnamnesisFormAction.OnSelectDewormingProduct(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        AnamnesisFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = catalogType,
                            catalogTypeName = catalogName
                        )
                    )
                },
                catalogosEmpty = baseList.isEmpty()
            )
        }

        // Sheet: Marca de concentrado
        if (state.isConcentrateBrandSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(AnamnesisFormAction.OnDismissConcentrateBrandSheet) },
                title = "Selecciona marca de concentrado",
                search = state.concentrateBrandSearchQuery,
                onSearchChange = { onAction(AnamnesisFormAction.OnSearchConcentrateBrandQueryChange(it)) },
                filteredAppCatalogs = state.filteredConcentrateBrands,
                selectedAppCatalog = state.formInputState.selectedFoodBrand,
                onSelectAppCatalog = { onAction(AnamnesisFormAction.OnSelectConcentrateBrand(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        AnamnesisFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.CONCENTRATE_BRAND_TYPE_CATALOG,
                            catalogTypeName = "Marca de concentrado"
                        )
                    )
                },
                catalogosEmpty = state.concentrateBrands.isEmpty()
            )
        }

        // Sheet: Unidad de medida de concentrado
        if (state.isConcentrateUnitSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(AnamnesisFormAction.OnDismissConcentrateUnitSheet) },
                title = "Selecciona unidad de medida",
                search = state.concentrateUnitSearchQuery,
                onSearchChange = { onAction(AnamnesisFormAction.OnSearchConcentrateUnitQueryChange(it)) },
                filteredAppCatalogs = state.filteredConcentrateUnits,
                selectedAppCatalog = state.formInputState.selectedFoodUnit,
                onSelectAppCatalog = { onAction(AnamnesisFormAction.OnSelectConcentrateUnit(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        AnamnesisFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG,
                            catalogTypeName = "Unidad de medida"
                        )
                    )
                },
                catalogosEmpty = state.concentrateUnitsOfMeasurement.isEmpty()
            )
        }

        // Diálogo para agregar cualquier catálogo al vuelo
        if (state.showAddAppCatalogDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddCatalog,
                catalogName = state.activeCatalogTypeName,
                onDismiss = { onAction(AnamnesisFormAction.OnDismissAddCatalogDialog) },
                onSave = { onAction(AnamnesisFormAction.OnSaveAppCatalog(name = it)) }
            )
        }

        // Diálogo de carga al registrar
        if (state.isLoadingSaveAnamnesis) {
            LoadingDialog(
                title = "Guardando Anamnesis...",
                subtitle = "Estamos sincronizando y guardando la información en la base de datos.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
    }
}