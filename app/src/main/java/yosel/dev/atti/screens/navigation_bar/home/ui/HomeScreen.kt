package yosel.dev.atti.screens.navigation_bar.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.SnackBarError
import yosel.dev.atti.core.navigation.main.Screens

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    snackBarHostState: SnackbarHostState,
    onAction: (HomeAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        BodyHome(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onAction = onAction,
            onNavigationMain = onNavigationMain
        )

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) { data ->
            SnackBarError(data = data)
        }
    }
}