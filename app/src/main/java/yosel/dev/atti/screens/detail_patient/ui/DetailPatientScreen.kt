package yosel.dev.atti.screens.detail_patient.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.DeleteConfirmationDialog
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.TopBarGlobal

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailPatientScreen(
    modifier: Modifier = Modifier,
    state: DetailPatientState,
    snackBarHostState: SnackbarHostState,
    onAction: (DetailPatientAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Detalle Paciente",
                onBack = onBack,
                actions = {
                    if (!state.isLoading && state.patient.id != ""){
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onAction(DetailPatientAction.OnEditClick) }
                            ) {

                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "editar"
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    onAction(
                                        DetailPatientAction.ToggleShowDialogConfirmDelete(show = true)
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when{
                        targetState.isLoading -> "LOADING"
                        targetState.patient.id.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "DetailPatientScreenAnimation"
            ){ targetState ->
                when{
                    targetState.isLoading ->{
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    targetState.patient.id.isEmpty() ->{
                        EmptyGlobal(
                            title = "No se pudo encontrar al paciente",
                            subTitle = "Intenta de nuevo más tarde",
                            icon = Icons.Outlined.Pets
                        )
                    }
                    else ->{
                        BodyDetailPatient(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                        )
                    }
                }
            }
        }

        if (state.showDialogConfirmDelete){
            DeleteConfirmationDialog(
                title = "Eliminar paciente",
                message = "¿Estás seguro de que deseas eliminar al paciente",
                itemTargetName = state.patient.name,
                warningNote = "Este registro se ocultará de los pacientes activos junto con toda su información vinculada. Podrás volver a activarlo en cualquier momento.",
                onConfirmDelete = { onAction(DetailPatientAction.DeletePatient) },
                onDismiss = { onAction(DetailPatientAction.ToggleShowDialogConfirmDelete(show = false)) },
                isLoading = state.isLoadingDeletePatient
            )
        }
    }
}