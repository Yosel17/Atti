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
import yosel.dev.atti.screens.top_level.clients.ui.ClientsEvent
import yosel.dev.atti.screens.top_level.clients.ui.ClientsScreen
import yosel.dev.atti.screens.top_level.clients.ui.ClientsViewModel
import yosel.dev.atti.screens.top_level.consultation.ui.ConsultationEvent
import yosel.dev.atti.screens.top_level.consultation.ui.ConsultationScreen
import yosel.dev.atti.screens.top_level.consultation.ui.ConsultationViewModel
import yosel.dev.atti.screens.top_level.home.ui.HomeEvent
import yosel.dev.atti.screens.top_level.home.ui.HomeScreen
import yosel.dev.atti.screens.top_level.home.ui.HomeViewModel
import yosel.dev.atti.screens.top_level.patients.ui.PatientsEvent
import yosel.dev.atti.screens.top_level.patients.ui.PatientsScreen
import yosel.dev.atti.screens.top_level.patients.ui.PatientsViewModel
import yosel.dev.atti.screens.top_level.products.ui.ProductsEvent
import yosel.dev.atti.screens.top_level.products.ui.ProductsScreen
import yosel.dev.atti.screens.top_level.products.ui.ProductsViewModel
import yosel.dev.atti.screens.top_level.services.ui.ServicesEvent
import yosel.dev.atti.screens.top_level.services.ui.ServicesScreen
import yosel.dev.atti.screens.top_level.services.ui.ServicesViewModel
import yosel.dev.atti.screens.top_level.suppliers.ui.SuppliersEvent
import yosel.dev.atti.screens.top_level.suppliers.ui.SuppliersScreen
import yosel.dev.atti.screens.top_level.suppliers.ui.SuppliersViewModel

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

fun EntryProviderScope<NavKey>.clientsEntry(
    onNavigationMain: (Screens) -> Unit
){
    entry<ScreensTopLevel.Clients> {
        val viewModel: ClientsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is ClientsEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
                is ClientsEvent.NavigateToPhone -> {
                    if (!context.dialPhoneNumber(event.phoneNumber)) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de teléfono"
                            )
                        }
                    }
                }
                is ClientsEvent.NavigateToWhatsapp -> {
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

        ClientsScreen(
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

fun EntryProviderScope<NavKey>.patientsEntry(
    onNavigationMain: (Screens) -> Unit
){
    entry<ScreensTopLevel.Patients>{
        val viewModel = hiltViewModel<PatientsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is PatientsEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
            }
        }

        PatientsScreen(
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

fun EntryProviderScope<NavKey>.productsEntry(
    onNavigationMain: (Screens) -> Unit
) {
    entry<ScreensTopLevel.Products> {
        val viewModel= hiltViewModel<ProductsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is ProductsEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
            }
        }

        ProductsScreen(
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

fun EntryProviderScope<NavKey>.servicesEntry(
    onNavigationMain: (Screens) -> Unit
) {
    entry<ScreensTopLevel.Services> {
        val viewModel= hiltViewModel<ServicesViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is ServicesEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
            }
        }

        ServicesScreen(
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

fun EntryProviderScope<NavKey>.suppliersEntry(
    onNavigationMain: (Screens) -> Unit
) {
    entry<ScreensTopLevel.Suppliers> {
        val viewModel = hiltViewModel<SuppliersViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is SuppliersEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
                is SuppliersEvent.NavigateToPhone -> {
                    if (!context.dialPhoneNumber(event.phoneNumber)) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de teléfono"
                            )
                        }
                    }
                }
                is SuppliersEvent.NavigateToWhatsapp -> {
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

        SuppliersScreen(
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