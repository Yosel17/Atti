package yosel.dev.atti.screens.fasting_form.ui

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
fun FastingFormScreen(
    modifier: Modifier = Modifier,
    state: FastingFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (FastingFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Ayuno" else "Ayuno",
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
                label = "FastingFormScreenAnimation"
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
                            subTitle = "No es posible registrar el ayuno sin los catálogos. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(FastingFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        if (state.existingFastingWithDetails != null && state.existingFastingWithDetails.fasting.status == Constants.DELETED_STATUS) {
                            EmptyGlobal(
                                title = "El ayuno se encuentra eliminado",
                                subTitle = "Esta ficha se encuentra eliminada y su información no se puede modificar.",
                                icon = Icons.Outlined.DeleteForever,
                                iconTint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            BodyFastingForm(
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

        // Sheet: Comida
        if (state.isFoodSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(FastingFormAction.OnDismissFoodSheet) },
                title = "Tiempo de ayuno (Comida)",
                search = state.foodSearchQuery,
                onSearchChange = { onAction(FastingFormAction.OnSearchFoodQueryChange(it)) },
                filteredAppCatalogs = state.filteredFoodCatalogs,
                selectedAppCatalog = state.formInputState.selectedFood,
                onSelectAppCatalog = { onAction(FastingFormAction.OnSelectFood(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        FastingFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.FOOD_FASTING_TYPE_CATALOG,
                            catalogTypeName = "Comida"
                        )
                    )
                },
                catalogosEmpty = state.foodCatalogs.isEmpty()
            )
        }

        // Sheet: Agua
        if (state.isWaterSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(FastingFormAction.OnDismissWaterSheet) },
                title = "Tiempo de ayuno (Agua)",
                search = state.waterSearchQuery,
                onSearchChange = { onAction(FastingFormAction.OnSearchWaterQueryChange(it)) },
                filteredAppCatalogs = state.filteredWaterCatalogs,
                selectedAppCatalog = state.formInputState.selectedWater,
                onSelectAppCatalog = { onAction(FastingFormAction.OnSelectWater(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        FastingFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.WATER_FASTING_TYPE_CATALOG,
                            catalogTypeName = "Agua"
                        )
                    )
                },
                catalogosEmpty = state.waterCatalogs.isEmpty()
            )
        }

        // Diálogo para agregar catálogo
        if (state.showAddAppCatalogDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddCatalog,
                catalogName = state.activeCatalogTypeName,
                onDismiss = { onAction(FastingFormAction.OnDismissAddCatalogDialog) },
                onSave = { onAction(FastingFormAction.OnSaveAppCatalog(name = it)) }
            )
        }

        // Diálogos de carga
        if (state.isLoadingSaveFasting) {
            LoadingDialog(
                title = "Guardando Ayuno...",
                subtitle = "Estamos sincronizando la información en la base de datos.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isLoadingUpdateFasting) {
            LoadingDialog(
                title = "Actualizando Ayuno...",
                subtitle = "Por favor espera un momento...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de confirmación
        if (state.showDialogConfirm) {
            SaveFastingDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                isEditMode = state.isEditMode,
                onDismiss = { onAction(FastingFormAction.ToggleSaveFastingDialog(show = false)) },
                onConfirm = {
                    onAction(FastingFormAction.ToggleSaveFastingDialog(show = false))
                    onAction(FastingFormAction.SaveFasting)
                }
            )
        }
    }
}