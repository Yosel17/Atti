package yosel.dev.atti.screens.detail_patient.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.TopBarGlobal

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
                    if (!state.isLoading && state.patient.id == ""){
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

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { onAction(DetailPatientAction.OnDeleteClick) }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "eliminar",
                                    tint = MaterialTheme.colorScheme.errorContainer
                                )
                            }

                        }
                    }
                }
            )
        }
    ) { }
}