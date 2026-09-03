package yosel.dev.atti.screens.prescription_form.ui

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
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PrescriptionFormScreen(
    modifier: Modifier = Modifier,
    state: PrescriptionFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (PrescriptionFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Receta" else "Resumen de Receta",
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
                        !targetState.isSuccessGetData -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "PrescriptionFormScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    !targetState.isSuccessGetData -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la información inicial",
                            subTitle = "No fue posible obtener los productos o presets necesarios. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(PrescriptionFormAction.TryLoadAgain) }
                        )
                    }
                    else -> {
                        BodyPrescriptionForm(
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

        // BottomSheet de Selección de Productos
        if (state.isProductSheetOpen) {
            SelectPrescriptionProductBottomSheet(
                onDismiss = { onAction(PrescriptionFormAction.OnDismissProductSheet) },
                search = state.productSearchQuery,
                onSearchChange = { onAction(PrescriptionFormAction.OnProductSearchQueryChange(it)) },
                filteredProducts = state.filteredProducts,
                tempSelectedProductIds = state.tempSelectedProductIds,
                onToggleSelectProduct = { onAction(PrescriptionFormAction.OnToggleSelectProduct(it)) },
                onConfirmSelection = { onAction(PrescriptionFormAction.OnConfirmProductSelection) },
                onAddCustomProductClick = {
                    onAction(PrescriptionFormAction.OnDismissProductSheet)
                    onAction(PrescriptionFormAction.OnOpenAddCustomProductDialog)
                },
                productsEmpty = state.productsWithDetails.isEmpty()
            )
        }

        // BottomSheet de Presets Rápidos
        if (state.isPresetSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(PrescriptionFormAction.OnDismissPresetSheet) },
                title = "Presets Rápidos",
                search = state.presetSearchQuery,
                onSearchChange = { onAction(PrescriptionFormAction.OnPresetSearchQueryChange(it)) },
                filteredAppCatalogs = state.filteredPresetCatalogs,
                selectedAppCatalog = null,
                onSelectAppCatalog = { onAction(PrescriptionFormAction.OnSelectPreset(it)) },
                showAddAppCatalogDialog = { onAction(PrescriptionFormAction.OnShowAddPresetDialog) },
                catalogosEmpty = state.presetCatalogs.isEmpty()
            )
        }

        // Diálogo para Agregar Nuevo Preset
        if (state.showAddPresetDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddPreset,
                catalogName = "Preset Rápido",
                onDismiss = { onAction(PrescriptionFormAction.OnDismissAddPresetDialog) },
                onSave = { onAction(PrescriptionFormAction.OnSavePresetCatalog(name = it)) }
            )
        }

        // Diálogo para Agregar Producto Fuera de Inventario
        if (state.showAddCustomProductDialog) {
            AddCustomProductDialog(
                onDismiss = { onAction(PrescriptionFormAction.OnDismissAddCustomProductDialog) },
                onConfirm = { name, instructions ->
                    onAction(PrescriptionFormAction.OnConfirmAddCustomProduct(name, instructions))
                }
            )
        }

        // Diálogos de Carga
        if (state.isLoadingSavePrescription) {
            LoadingDialog(
                title = "Guardando Receta...",
                subtitle = "Estamos registrando los productos e instrucciones en el expediente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdatePrescription) {
            LoadingDialog(
                title = "Actualizando Receta...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de Confirmación
        if (state.showDialogConfirm) {
            SavePrescriptionDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                itemsCount = state.formInputState.selectedItems.size,
                isEditMode = state.isEditMode,
                onDismiss = { onAction(PrescriptionFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(PrescriptionFormAction.ToggleSaveDialog(show = false))
                    onAction(PrescriptionFormAction.SavePrescription)
                }
            )
        }
    }
}