package yosel.dev.atti.screens.physio_consts_form.ui

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
import androidx.compose.material.icons.outlined.DeleteForever
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
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PhysioConstsFormScreen(
    modifier: Modifier = Modifier,
    state: PhysioConstsFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (PhysioConstsFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Constantes" else "Constantes Fisiológicas",
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
                label = "PhysiolConstsScreenAnimation"
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
                            subTitle = "No es posible registrar las constantes sin esa información. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(PhysioConstsFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        if (state.currentConstants != null && state.currentConstants.status == Constants.DELETED_STATUS) {
                            EmptyGlobal(
                                title = "Las constantes se encuentran eliminadas",
                                subTitle = "Esta ficha se encuentra eliminada y su información no se puede modificar.",
                                icon = Icons.Outlined.DeleteForever,
                                iconTint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            BodyPhysiologicalConstsForm(
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
        }

        // BottomSheet: Selección de unidad de peso
        if (state.isWeightUnitSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(PhysioConstsFormAction.OnDismissWeightUnitSheet) },
                title = "Selecciona unidad de peso",
                search = state.weightUnitSearchQuery,
                onSearchChange = { onAction(PhysioConstsFormAction.OnSearchWeightUnitQueryChange(it)) },
                filteredAppCatalogs = state.filteredWeightUnits,
                selectedAppCatalog = state.formInputState.selectedWeightUnit,
                onSelectAppCatalog = { onAction(PhysioConstsFormAction.OnSelectWeightUnit(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        PhysioConstsFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.UNIT_OF_WEIGHT_TYPE_CATALOG,
                            catalogTypeName = "Unidad de peso"
                        )
                    )
                },
                catalogosEmpty = state.weightUnits.isEmpty()
            )
        }

        // Diálogo para agregar nueva unidad de peso
        if (state.showAddAppCatalogDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddCatalog,
                catalogName = state.activeCatalogTypeName,
                onDismiss = { onAction(PhysioConstsFormAction.OnDismissAddCatalogDialog) },
                onSave = { onAction(PhysioConstsFormAction.OnSaveAppCatalog(name = it)) }
            )
        }

        // Diálogos de carga
        if (state.isLoadingSave) {
            LoadingDialog(
                title = "Guardando Constantes...",
                subtitle = "Estamos guardando la información en la base de datos.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdate) {
            LoadingDialog(
                title = "Actualizando Constantes...",
                subtitle = "Por favor espera un momento...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de confirmación
        if (state.showDialogConfirm) {
            SavePhysiologicalConstsDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                isEditMode = state.isEditMode,
                onDismiss = { onAction(PhysioConstsFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(PhysioConstsFormAction.ToggleSaveDialog(show = false))
                    onAction(PhysioConstsFormAction.SaveConstants)
                }
            )
        }
    }
}