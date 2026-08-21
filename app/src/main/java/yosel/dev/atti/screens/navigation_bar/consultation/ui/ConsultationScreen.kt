package yosel.dev.atti.screens.navigation_bar.consultation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.SnackBarError

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConsultationScreen(
    modifier: Modifier = Modifier,
    state: ConsultationState,
    snackBarHostState: SnackbarHostState,
    onAction: (ConsultationAction) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state.isLoadingData,
            label = "ConsultationContentAnimation"
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator(modifier = Modifier.size(64.dp))
                }
            } else if (state.consultationReasons.isEmpty()) {
                EmptyGlobal(
                    title = "Sin motivos de consulta",
                    subTitle = "No se encontraron catálogos de consulta disponibles.",
                    icon = Icons.Outlined.MedicalServices,
                    showAction = true,
                    onClickAction = { onAction(ConsultationAction.OnRetryInitialData) }
                )
            } else {
                BodyConsultation(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onAction = onAction
                )
            }
        }

        if (state.showConfirmDialog && state.pendingSelectedReason != null && state.selectedPatient != null) {
            ConfirmStartConsultationDialog(
                patientName = state.selectedPatient.patient.name,
                reasonName = state.pendingSelectedReason.name,
                isLoading = state.isStartingConsultation,
                onConfirm = { onAction(ConsultationAction.OnConfirmStartConsultation) },
                onDismiss = { onAction(ConsultationAction.OnDismissConfirmDialog) }
            )
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            SnackBarError(data = data)
        }
    }
}