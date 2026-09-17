package yosel.dev.atti.core.navigation.top_level

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import yosel.dev.atti.core.components.SnackbarType
import yosel.dev.atti.core.components.showCustomSnackbar
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.ObserveAsEvents
import yosel.dev.atti.core.utils.dialPhoneNumber
import yosel.dev.atti.core.utils.openWhatsApp
import yosel.dev.atti.screens.top_level.consultation.ui.ConsultationEvent
import yosel.dev.atti.screens.top_level.consultation.ui.ConsultationScreen
import yosel.dev.atti.screens.top_level.consultation.ui.ConsultationViewModel
import yosel.dev.atti.screens.top_level.directory.ui.DirectoryEvent
import yosel.dev.atti.screens.top_level.directory.ui.DirectoryScreen
import yosel.dev.atti.screens.top_level.directory.ui.DirectoryViewModel
import yosel.dev.atti.screens.top_level.home.ui.HomeEvent
import yosel.dev.atti.screens.top_level.home.ui.HomeScreen
import yosel.dev.atti.screens.top_level.home.ui.HomeViewModel
import yosel.dev.atti.screens.top_level.inventory.ui.InventoryEvent
import yosel.dev.atti.screens.top_level.inventory.ui.InventoryScreen
import yosel.dev.atti.screens.top_level.inventory.ui.InventoryViewModel

fun EntryProviderScope<NavKey>.homeEntry(
    onNavigationMain: (Screens) -> Unit
){
    entry<ScreensTopLevel.Home> {
        val viewModel = hiltViewModel<HomeViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when(event) {
                is HomeEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
            }
        }

        HomeScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction,
            onNavigationMain = onNavigationMain
        )
    }
}

fun EntryProviderScope<NavKey>.directoryEntry(
    onNavigationMain: (Screens) -> Unit
){
    entry<ScreensTopLevel.Directory> {
        val viewModel = hiltViewModel<DirectoryViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when(event){
                is DirectoryEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
                is DirectoryEvent.NavigateToPhone -> {
                    if (!context.dialPhoneNumber(event.phoneNumber)) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de teléfono"
                            )
                        }
                    }
                }
                is DirectoryEvent.NavigateToWhatsapp -> {
                    if (!context.openWhatsApp(event.phoneNumber)) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de WhatsApp"
                            )
                        }
                    }
                }
            }
        }

        DirectoryScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onNavigationMain = onNavigationMain,
            onAction = viewModel::onAction
        )
    }
}

fun EntryProviderScope<NavKey>.consultationEntry(
    onNavigationMain: (Screens) -> Unit
) {
    entry<ScreensTopLevel.Consultation> {
        val viewModel = hiltViewModel<ConsultationViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is ConsultationEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is ConsultationEvent.ShowSnackBarSuccess -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
            }
        }

        ConsultationScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction,
            onNavigationMain = onNavigationMain
        )
    }
}

fun EntryProviderScope<NavKey>.inventoryEntry(
    onNavigationMain: (Screens) -> Unit
){
    entry<ScreensTopLevel.Inventory> {
        val viewModel = hiltViewModel<InventoryViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is InventoryEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
                is InventoryEvent.NavigateToPhone -> {
                    if (!context.dialPhoneNumber(event.phoneNumber)) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de teléfono"
                            )
                        }
                    }
                }
                is InventoryEvent.NavigateToWhatsapp -> {
                    if (!context.openWhatsApp(event.phoneNumber)) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de WhatsApp"
                            )
                        }
                    }
                }
            }
        }

        InventoryScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction,
            onNavigationMain = onNavigationMain
        )
    }
}