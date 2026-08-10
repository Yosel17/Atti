package yosel.dev.atti.screens.add_patient.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AddCatalogBottomSheet
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.AttiTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddPatientScreen(
    modifier: Modifier = Modifier,
    state: AddPatientState,
    snackBarHostState: SnackbarHostState,
    onAction: (AddPatientAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Paciente" else "Registrar Paciente",
                onBack = onBack
            )
        }
    ){ paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
        ){
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when{
                        targetState.isLoadingDataInitial -> "LOADING"
                        targetState.speciesCatalog.isEmpty() && targetState.genderCatalog.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "AddPatientScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    targetState.speciesCatalog.isEmpty() || targetState.genderCatalog.isEmpty() || targetState.clients.isEmpty() -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la información inicial",
                            subTitle = "No es posible registrar pacientes sin esa información. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(AddPatientAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        if (state.currentPatient != null && state.currentPatient.status == Constants.DELETED_PATIENT_STATUS){
                            EmptyGlobal(
                                title = "El paciente ${state.currentPatient.name} se encuentra eliminado",
                                subTitle = "Este paciente se encuentra eliminado y su información no se puede modificar. Restablécelo para poder editarlo.",
                                icon = Icons.Outlined.DeleteForever,
                                iconTint = MaterialTheme.colorScheme.error
                            )
                        }else{
                            BodyAddPatient(
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

        if (state.isLoadingRegister) {
            LoadingDialog(
                title = "Registrando paciente...",
                subtitle = "Estamos guardando la información del nuevo paciente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isLoadingAddCatalog) {
            LoadingDialog(
                title = "Guardando ${state.activeCatalogTypeName.lowercase()}...",
                subtitle = "Por favor espera un momento...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isLoadingUpdatePatient) {
            LoadingDialog(
                title = "Actualizando paciente...",
                subtitle = "Por favor espera un momento...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isClientSheetOpen) {
            SelectClientBottomSheet(
                state = state,
                onAction = onAction
            )
        }

        if (state.isAddCatalogSheetOpen) {
            AddCatalogBottomSheet(
                catalogName = state.activeCatalogTypeName,
                onDismiss = { onAction(AddPatientAction.OnDismissAddCatalogSheet) },
                onSave = { name -> onAction(AddPatientAction.OnSaveCatalog(name)) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ScreenPreview() {
    AttiTheme {
        AddPatientScreen(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            state = AddPatientState(
                isLoadingDataInitial = false,
                speciesCatalog = listOf(
                    AppCatalogModel(
                        id = 1,
                        name = "Canino"
                    )
                ),
                genderCatalog = listOf(
                    AppCatalogModel(
                        id = 4,
                        name = "Macho"
                    )
                ),
                clients = listOf(
                    ClientModel(
                        id = "adfjlkadfj-adfnkladfjn-afshdfa",
                        firstName = "Carlos Yosel",
                        lastName = "Alvizures Bran"
                    )
                )
            ),
            snackBarHostState = SnackbarHostState(),
            onAction = {},
            onBack = {}
        )
    }
}