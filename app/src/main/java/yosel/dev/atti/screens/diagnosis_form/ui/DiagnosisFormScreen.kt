package yosel.dev.atti.screens.diagnosis_form.ui

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
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DiagnosisFormScreen(
    modifier: Modifier = Modifier,
    state: DiagnosisFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (DiagnosisFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Diagnóstico" else "Diagnóstico",
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
                label = "DiagnosisFormScreenAnimation"
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
                            subTitle = "No es posible registrar el diagnóstico sin los catálogos. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(DiagnosisFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        BodyDiagnosisForm(
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

        // Diálogos de carga
        if (state.isLoadingAddTag) {
            LoadingDialog(
                title = "Guardando etiqueta...",
                subtitle = "Agregando el nuevo diagnóstico a la base de datos.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isLoadingSaveDiagnosis) {
            LoadingDialog(
                title = "Guardando Diagnóstico...",
                subtitle = "Estamos sincronizando la información en la base de datos.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isLoadingUpdateDiagnosis) {
            LoadingDialog(
                title = "Actualizando Diagnóstico...",
                subtitle = "Por favor espera un momento...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de confirmación
        if (state.showDialogConfirm) {
            SaveDiagnosisDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                selectedCount = state.formInputState.selectedDiagnoses.size,
                isEditMode = state.isEditMode,
                onDismiss = { onAction(DiagnosisFormAction.ToggleSaveDiagnosisDialog(show = false)) },
                onConfirm = {
                    onAction(DiagnosisFormAction.ToggleSaveDiagnosisDialog(show = false))
                    onAction(DiagnosisFormAction.SaveDiagnosis)
                }
            )
        }
    }
}