package yosel.dev.atti.screens.top_level.patients.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.PatientFilterBottomSheet
import yosel.dev.atti.core.components.SnackBarError
import yosel.dev.atti.core.navigation.main.Screens

@Composable
fun PatientsScreen(
    modifier: Modifier = Modifier,
    state: PatientsState,
    snackBarHostState: SnackbarHostState,
    onAction: (PatientsAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {

    Box(modifier = modifier.fillMaxSize()) {
        BodyPatients(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onAction = onAction,
            onNavigationMain = onNavigationMain
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd),
            visible = state.patients.isNotEmpty() && !state.isLoading,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)),
            exit = slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ) + scaleOut(
                targetScale = 0.8f,
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            ExtendedFloatingActionButton(
                onClick = { onNavigationMain(Screens.AddPatient()) },
                icon = { Icon(imageVector = Icons.Filled.Pets, contentDescription = "Nuevo paciente") },
                text = { Text(text = "Agregar paciente") },
                expanded = true,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
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

    if (state.showFilterSheet) {
        PatientFilterBottomSheet(
            initialFilter = state.filter,
            availableSpecies = state.availableSpecies,
            availableGenders = state.availableGenders,
            onDismissRequest = { onAction(PatientsAction.OnToggleFilterSheet(false)) },
            onApply = { onAction(PatientsAction.OnApplyFilter(it)) }
        )
    }
}