package yosel.dev.atti.screens.clinical_exam_form.ui

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
import yosel.dev.atti.core.components.SelectAppCatalogMultiBottomSheet
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClinicalExamFormScreen(
    modifier: Modifier = Modifier,
    state: ClinicalExamFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (ClinicalExamFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Examen Clínico" else "Nuevo Examen Clínico",
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
                label = "ClinicalExamScreenAnimation"
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
                            subTitle = "No es posible registrar el examen clínico sin esa información. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(ClinicalExamFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        if (state.currentExam != null && state.currentExam.status == Constants.DELETED_STATUS) {
                            EmptyGlobal(
                                title = "El examen clínico se encuentra eliminado",
                                subTitle = "Esta ficha se encuentra eliminada y su información no se puede modificar.",
                                icon = Icons.Outlined.DeleteForever,
                                iconTint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            BodyClinicalExamForm(
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

        // Sheet: Pelaje
        if (state.isCoatSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(ClinicalExamFormAction.OnDismissCoatSheet) },
                title = "Selecciona tipo de pelaje",
                search = state.coatSearchQuery,
                onSearchChange = { onAction(ClinicalExamFormAction.OnSearchCoatQueryChange(it)) },
                filteredAppCatalogs = state.filteredCoatCatalogs,
                selectedAppCatalog = state.formInputState.selectedCoat,
                onSelectAppCatalog = { onAction(ClinicalExamFormAction.OnSelectCoat(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        ClinicalExamFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.COAT_TYPE_CATALOG,
                            catalogTypeName = "Pelaje"
                        )
                    )
                },
                catalogosEmpty = state.coatCatalogs.isEmpty()
            )
        }

        // Sheet: Nódulos Linfáticos Infartados
        if (state.isLymphNodeSheetOpen) {
            SelectAppCatalogMultiBottomSheet(
                onDismiss = { onAction(ClinicalExamFormAction.OnDismissLymphNodesSheet) },
                title = "Nódulos linfáticos",
                search = state.lymphNodeSearchQuery,
                onSearchChange = { onAction(ClinicalExamFormAction.OnSearchLymphNodesQueryChange(it)) },
                filteredAppCatalogs = state.filteredLymphNodeCatalogs,
                selectedAppCatalogs = state.formInputState.selectedLymphNodes,
                onToggleAppCatalog = { onAction(ClinicalExamFormAction.OnToggleLymphNodeOption(it)) },
                showAddAppCatalogDialog = {
                    onAction(
                        ClinicalExamFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.LYMPH_NODE_TYPE_CATALOG,
                            catalogTypeName = "Nódulo linfático"
                        )
                    )
                },
                catalogosEmpty = state.lymphNodeCatalogs.isEmpty()
            )
        }

        // Diálogo para agregar catálogo
        if (state.showAddAppCatalogDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddCatalog,
                catalogName = state.activeCatalogTypeName,
                onDismiss = { onAction(ClinicalExamFormAction.OnDismissAddCatalogDialog) },
                onSave = { onAction(ClinicalExamFormAction.OnSaveAppCatalog(name = it)) }
            )
        }

        // Diálogos de carga
        if (state.isLoadingSaveExam) {
            LoadingDialog(
                title = "Guardando Examen Clínico...",
                subtitle = "Estamos sincronizando y guardando la información en la base de datos.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdateExam) {
            LoadingDialog(
                title = "Actualizando Examen Clínico...",
                subtitle = "Por favor espera un momento...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de confirmación
        if (state.showDialogConfirm) {
            SaveClinicalExamDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                isEditMode = state.isEditMode,
                onDismiss = { onAction(ClinicalExamFormAction.ToggleSaveExamDialog(show = false)) },
                onConfirm = {
                    onAction(ClinicalExamFormAction.ToggleSaveExamDialog(show = false))
                    onAction(ClinicalExamFormAction.SaveClinicalExam)
                }
            )
        }
    }
}