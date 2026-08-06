package yosel.dev.atti.screens.detail_client.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.ui.theme.AttiTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailClientScreen(
    modifier: Modifier = Modifier,
    state: DetailClientState,
    snackBarHostState: SnackbarHostState,
    onAction: (DetailClientAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        if (!state.isLoading && state.clientWithPatients.client.id.isNotEmpty()){
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "editar"
                            )
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
                        targetState.clientWithPatients.client.id.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "DetailClientScreenAnimation"
            ){ targetState ->
                when {
                    targetState.isLoading ->{
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    targetState.clientWithPatients.client.id.isEmpty() ->{
                        EmptyGlobal(
                            title = "No se pudo encontrar al cliente",
                            subTitle = "Intenta de nuevo más tarde"
                        )
                    }
                    else ->{
                        BodyDetailClient(
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
fun DetailClientScreenPreview() {
    AttiTheme {
        DetailClientScreen(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            state = DetailClientState(
                isLoading = false
            ),
            snackBarHostState = SnackbarHostState(),
            onAction = {},
            onBack = {}
        )
    }
}