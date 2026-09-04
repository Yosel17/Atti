package yosel.dev.atti.screens.follow_up_form.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AddAppCatalogDialog
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.SelectAppCatalogBottomSheet
import yosel.dev.atti.core.components.TopBarGlobal
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FollowUpFormScreen(
    modifier: Modifier = Modifier,
    state: FollowUpFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (FollowUpFormAction) -> Unit,
    onBack: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM, yyyy", Locale.forLanguageTag("es-ES")) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("hh:mm a", Locale.US) }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Reconsulta" else "Asignar Reconsulta",
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
                label = "FollowUpFormScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    !targetState.isSuccessGetData -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la consulta",
                            subTitle = "Ocurrió un error al cargar la información del paciente. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(FollowUpFormAction.TryLoadAgain) }
                        )
                    }
                    else -> {
                        BodyFollowUpForm(
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

        // Diálogo DatePicker propio de Material 3
        if (state.showDatePickerDialog) {
            FollowUpDatePickerDialog(
                initialDate = state.formInputState.selectedDate,
                onDateSelected = { selectedDate ->
                    onAction(FollowUpFormAction.OnSelectDate(selectedDate))
                },
                onDismiss = {
                    onAction(FollowUpFormAction.ToggleDatePickerDialog(show = false))
                }
            )
        }

        // BottomSheet Motivos Rápidos (Catálogo 20)
        if (state.isQuickReasonSheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = { onAction(FollowUpFormAction.OnDismissQuickReasonSheet) },
                title = "Motivos de Consulta Rápidos",
                search = state.quickReasonSearchQuery,
                onSearchChange = { onAction(FollowUpFormAction.OnQuickReasonSearchQueryChange(it)) },
                filteredAppCatalogs = state.filteredQuickReasonCatalogs,
                selectedAppCatalog = null,
                onSelectAppCatalog = { onAction(FollowUpFormAction.OnSelectQuickReason(it)) },
                showAddAppCatalogDialog = { onAction(FollowUpFormAction.OnShowAddQuickReasonDialog) },
                catalogosEmpty = state.quickReasonCatalogs.isEmpty()
            )
        }

        // Diálogo para Agregar Nuevo Motivo Rápido
        if (state.showAddQuickReasonDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddQuickReason,
                catalogName = "Motivo Rápido",
                onDismiss = { onAction(FollowUpFormAction.OnDismissAddQuickReasonDialog) },
                onSave = { onAction(FollowUpFormAction.OnSaveQuickReasonCatalog(name = it)) }
            )
        }

        // Diálogos de Carga
        if (state.isLoadingSaveFollowUp) {
            LoadingDialog(
                title = "Agendando Cita...",
                subtitle = "Estamos registrando la fecha y hora de la reconsulta en el expediente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdateFollowUp) {
            LoadingDialog(
                title = "Actualizando Cita...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de Confirmación
        if (state.showDialogConfirm) {
            val formattedDate = state.formInputState.selectedDate.format(dateFormatter)
                .replaceFirstChar { it.uppercase() }
            val formattedTime = state.formInputState.selectedTime.format(timeFormatter).uppercase()

            SaveFollowUpDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                scheduledDate = formattedDate,
                scheduledTime = formattedTime,
                reason = state.formInputState.reason.trim(),
                isEditMode = state.isEditMode,
                onDismiss = { onAction(FollowUpFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(FollowUpFormAction.ToggleSaveDialog(show = false))
                    onAction(FollowUpFormAction.SaveFollowUp)
                }
            )
        }
    }
}