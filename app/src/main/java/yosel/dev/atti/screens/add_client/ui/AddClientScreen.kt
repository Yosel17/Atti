package yosel.dev.atti.screens.add_client.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddClientScreen(
    modifier: Modifier = Modifier,
    state: AddClientState,
    snackBarHostState: SnackbarHostState,
    onAction: (AddClientAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {

        }
    ) { paddingValues ->  }
}