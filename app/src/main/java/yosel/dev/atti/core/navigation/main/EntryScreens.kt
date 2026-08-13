package yosel.dev.atti.core.navigation.main

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
import yosel.dev.atti.core.utils.ObserveAsEvents
import yosel.dev.atti.core.utils.dialPhoneNumber
import yosel.dev.atti.core.utils.openWhatsApp
import yosel.dev.atti.screens.add_client.ui.AddClientEvent
import yosel.dev.atti.screens.add_client.ui.AddClientScreen
import yosel.dev.atti.screens.add_client.ui.AddClientViewModel
import yosel.dev.atti.screens.add_patient.ui.AddPatientEvent
import yosel.dev.atti.screens.add_patient.ui.AddPatientScreen
import yosel.dev.atti.screens.add_patient.ui.AddPatientViewModel
import yosel.dev.atti.screens.add_supplier.ui.AddSupplierEvent
import yosel.dev.atti.screens.add_supplier.ui.AddSupplierScreen
import yosel.dev.atti.screens.add_supplier.ui.AddSupplierViewModel
import yosel.dev.atti.screens.detail_client.ui.DetailClientEvent
import yosel.dev.atti.screens.detail_client.ui.DetailClientScreen
import yosel.dev.atti.screens.detail_client.ui.DetailClientViewModel
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientEvent
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientScreen
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientViewModel
import yosel.dev.atti.screens.detail_supplier.ui.DetailSupplierEvent
import yosel.dev.atti.screens.detail_supplier.ui.DetailSupplierScreen
import yosel.dev.atti.screens.detail_supplier.ui.DetailSupplierViewModel
import yosel.dev.atti.screens.main.ui.MainScreen
import yosel.dev.atti.screens.product_form.ui.ProductFormEvent
import yosel.dev.atti.screens.product_form.ui.ProductFormScreen
import yosel.dev.atti.screens.product_form.ui.ProductFormViewModel

fun EntryProviderScope<NavKey>.mainEntry(
    onNavigation: (Screens) -> Unit,
) {
    entry<Screens.Main> {
        MainScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            onNavigationMain = onNavigation
        )
    }
}

fun EntryProviderScope<NavKey>.addClientEntry(
    onBack: () -> Unit
) {
    entry<Screens.AddClient> {
        val viewModel = hiltViewModel<AddClientViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is AddClientEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }

                is AddClientEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
            }
        }

        AddClientScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}

fun EntryProviderScope<NavKey>.detailClientEntry(
    onBack: () -> Unit,
    onNavigation: (Screens) -> Unit
) {
    entry<Screens.DetailClient> { detailClientKey ->
        val viewModel: DetailClientViewModel = hiltViewModel(
            creationCallback = { factory: DetailClientViewModel.Factory ->
                factory.create(
                    clienteId = detailClientKey.clientId,
                    isLocalPatients = detailClientKey.isLocalPatients
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DetailClientEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }

                is DetailClientEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }

                is DetailClientEvent.OnCallClick -> {
                    if (!context.dialPhoneNumber(event.phoneNumber)) {
                        scope.launch {
                            snackbarHostState.showCustomSnackbar(
                                message = "No se puede abrir la aplicación de teléfono",
                                type = SnackbarType.ERROR
                            )
                        }
                    }
                }

                is DetailClientEvent.OnWhatsappClick -> {
                    if (!context.openWhatsApp(event.phoneNumber)) {
                        scope.launch {
                            snackbarHostState.showCustomSnackbar(
                                message = "No se puede abrir la aplicación de WhatsApp",
                                type = SnackbarType.ERROR
                            )
                        }
                    }
                }

                is DetailClientEvent.OnNavigationMain -> {
                    onNavigation(event.screen)
                }
            }
        }

        DetailClientScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackbarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}

fun EntryProviderScope<NavKey>.addPatientEntry(
    onBack: () -> Unit
) {
    entry<Screens.AddPatient> { addPatientKey ->
        val viewModel: AddPatientViewModel = hiltViewModel(
            creationCallback = { factory: AddPatientViewModel.Factory ->
                factory.create(
                    patientId = addPatientKey.patientId,
                    clienteId = addPatientKey.clientId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is AddPatientEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }

                is AddPatientEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
            }
        }

        AddPatientScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}

fun EntryProviderScope<NavKey>.detailPatientEntry(
    onBack: () -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    entry<Screens.DetailPatient> { detailPatientKey ->

        val viewModel: DetailPatientViewModel = hiltViewModel(
            creationCallback = { factory: DetailPatientViewModel.Factory ->
                factory.create(
                    patientId = detailPatientKey.patientId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DetailPatientEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }

                is DetailPatientEvent.OnNavigationMain -> {
                    onNavigationMain(event.screen)
                }

                is DetailPatientEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
            }
        }

        DetailPatientScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackbarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}

fun EntryProviderScope<NavKey>.addSupplierEntry(
    onBack: () -> Unit
) {
    entry<Screens.AddSupplier> {
        val viewModel = hiltViewModel<AddSupplierViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is AddSupplierEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }

                is AddSupplierEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackBarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
            }
        }

        AddSupplierScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}

fun EntryProviderScope<NavKey>.detailSupplierEntry(
    onBack: () -> Unit
) {
    entry<Screens.DetailSupplier> { detailSupplierKey ->
        val viewModel: DetailSupplierViewModel = hiltViewModel(
            creationCallback = { factory: DetailSupplierViewModel.Factory ->
                factory.create(
                    supplierId = detailSupplierKey.supplierId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DetailSupplierEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is DetailSupplierEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is DetailSupplierEvent.OnCallClick -> {
                    if (!context.dialPhoneNumber(event.phoneNumber)) {
                        scope.launch {
                            snackbarHostState.showCustomSnackbar(
                                message = "No se puede abrir la aplicación de teléfono",
                                type = SnackbarType.ERROR
                            )
                        }
                    }
                }
                is DetailSupplierEvent.OnWhatsappClick -> {
                    if (!context.openWhatsApp(event.phoneNumber)) {
                        scope.launch {
                            snackbarHostState.showCustomSnackbar(
                                message = "No se puede abrir la aplicación de WhatsApp",
                                type = SnackbarType.ERROR
                            )
                        }
                    }
                }
            }
        }

        DetailSupplierScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackbarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )

    }
}

fun EntryProviderScope<NavKey>.productFormEntry(
    onBack: () -> Unit
){
    entry<Screens.ProductForm> { productFormKey ->
        val viewModel: ProductFormViewModel = hiltViewModel(
            creationCallback = { factory: ProductFormViewModel.Factory ->
                factory.create(
                    productId = productFormKey.productId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when(event){
                is ProductFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is ProductFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
            }
        }

        ProductFormScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackbarHostState,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}