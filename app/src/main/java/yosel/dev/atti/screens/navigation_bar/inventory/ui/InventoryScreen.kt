package yosel.dev.atti.screens.navigation_bar.inventory.ui

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

@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    state: InventoryState,
    snackBarHostState: SnackbarHostState,
    onAction: (InventoryAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BodyInventory(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onAction = onAction
        )

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