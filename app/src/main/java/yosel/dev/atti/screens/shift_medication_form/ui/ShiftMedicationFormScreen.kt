package yosel.dev.atti.screens.shift_medication_form.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShiftMedicationFormScreen(
    modifier: Modifier = Modifier,
    state: ShiftMedicationFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (ShiftMedicationFormAction) -> Unit,
    onBack: () -> Unit,
    onNavigation: (Screens) -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Fármacos de Turno" else "Fármacos de Turno",
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
                label = "ShiftMedicationFormScreenAnimation"
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
                            subTitle = "No fue posible obtener los productos o servicios necesarios. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(ShiftMedicationFormAction.TryLoadAgain) }
                        )
                    }
                    else -> {
                        BodyShiftMedicationForm(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                            onAction = onAction,
                            onNavigation = onNavigation
                        )
                    }
                }
            }
        }

        // BottomSheet Productos
        if (state.isProductSheetOpen) {
            SelectProductBottomSheet(
                onDismiss = { onAction(ShiftMedicationFormAction.OnDismissProductSheet) },
                search = state.productSearchQuery,
                onSearchChange = { onAction(ShiftMedicationFormAction.OnProductSearchQueryChange(it)) },
                filteredProducts = state.filteredProducts,
                tempSelectedProductIds = state.tempSelectedProductIds,
                onToggleSelectProduct = { onAction(ShiftMedicationFormAction.OnToggleSelectProduct(it)) },
                onConfirmSelection = { onAction(ShiftMedicationFormAction.OnConfirmProductSelection) },
                productsEmpty = state.productsWithDetails.isEmpty()
            )
        }

        // BottomSheet Servicios
        if (state.isServiceSheetOpen) {
            SelectServiceBottomSheet(
                onDismiss = { onAction(ShiftMedicationFormAction.OnDismissServiceSheet) },
                search = state.serviceSearchQuery,
                onSearchChange = { onAction(ShiftMedicationFormAction.OnServiceSearchQueryChange(it)) },
                filteredServices = state.filteredServices,
                tempSelectedServiceIds = state.tempSelectedServiceIds,
                onToggleSelectService = { onAction(ShiftMedicationFormAction.OnToggleSelectService(it)) },
                onConfirmSelection = { onAction(ShiftMedicationFormAction.OnConfirmServiceSelection) },
                servicesEmpty = state.servicesWithDetails.isEmpty()
            )
        }

        // Diálogos de Carga
        if (state.isLoadingSaveShiftMedication) {
            LoadingDialog(
                title = "Guardando Fármacos de Turno...",
                subtitle = "Estamos registrando los productos y servicios en el expediente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdateShiftMedication) {
            LoadingDialog(
                title = "Actualizando Fármacos de Turno...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de Confirmación
        if (state.showDialogConfirm) {
            SaveShiftMedicationDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                productsCount = state.formInputState.selectedProducts.size,
                servicesCount = state.formInputState.selectedServices.size,
                totalPrice = state.formInputState.totalAmount,
                isEditMode = state.isEditMode,
                onDismiss = { onAction(ShiftMedicationFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(ShiftMedicationFormAction.ToggleSaveDialog(show = false))
                    onAction(ShiftMedicationFormAction.SaveShiftMedication)
                }
            )
        }
    }
}
