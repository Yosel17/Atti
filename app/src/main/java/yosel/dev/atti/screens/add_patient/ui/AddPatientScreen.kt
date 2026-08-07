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
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.TopBarGlobal
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
                title = "Registrar Paciente",
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
                        targetState.isLoadingCatalogs -> "LOADING"
                        targetState.speciesCatalog.isEmpty() && targetState.genderCatalog.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "AddPatientScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingCatalogs -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    targetState.speciesCatalog.isEmpty() && targetState.genderCatalog.isEmpty() -> {
                        EmptyGlobal(
                            title = "No se pudieron cargar los catálogos",
                            subTitle = "No es posible registrar pacientes sin los catálogos. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(AddPatientAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
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
}

@PreviewLightDark
@Composable
private fun ScreenPreview() {
    AttiTheme {
        AddPatientScreen(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            state = AddPatientState(
                isLoadingCatalogs = false
            ),
            snackBarHostState = SnackbarHostState(),
            onAction = {},
            onBack = {}
        )
    }
}