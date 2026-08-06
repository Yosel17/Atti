package yosel.dev.atti.screens.add_client.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.TopBarGlobal

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
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Registrar Cliente",
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
        ){
            BodyAddClient(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                state = state,
                onAction = onAction
            )
        }

        if (state.isLoadingAddClient){
            LoadingDialog(
                title = "Registrando Cliente...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
    }
}