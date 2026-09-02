package yosel.dev.atti.core.navigation.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
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
import yosel.dev.atti.screens.anamnesis_form.ui.AnamnesisFormEvent
import yosel.dev.atti.screens.anamnesis_form.ui.AnamnesisFormScreen
import yosel.dev.atti.screens.anamnesis_form.ui.AnamnesisFormViewModel
import yosel.dev.atti.screens.clinical_exam_form.ui.ClinicalExamFormEvent
import yosel.dev.atti.screens.clinical_exam_form.ui.ClinicalExamFormScreen
import yosel.dev.atti.screens.clinical_exam_form.ui.ClinicalExamFormViewModel
import yosel.dev.atti.screens.detail_client.ui.DetailClientEvent
import yosel.dev.atti.screens.detail_client.ui.DetailClientScreen
import yosel.dev.atti.screens.detail_client.ui.DetailClientViewModel
import yosel.dev.atti.screens.detail_consultation.ui.DetailConsultationEvent
import yosel.dev.atti.screens.detail_consultation.ui.DetailConsultationScreen
import yosel.dev.atti.screens.detail_consultation.ui.DetailConsultationViewModel
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientEvent
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientScreen
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientViewModel
import yosel.dev.atti.screens.detail_product.ui.DetailProductEvent
import yosel.dev.atti.screens.detail_product.ui.DetailProductScreen
import yosel.dev.atti.screens.detail_product.ui.DetailProductViewModel
import yosel.dev.atti.screens.detail_service.ui.DetailServiceEvent
import yosel.dev.atti.screens.detail_service.ui.DetailServiceScreen
import yosel.dev.atti.screens.detail_service.ui.DetailServiceViewModel
import yosel.dev.atti.screens.detail_supplier.ui.DetailSupplierEvent
import yosel.dev.atti.screens.detail_supplier.ui.DetailSupplierScreen
import yosel.dev.atti.screens.detail_supplier.ui.DetailSupplierViewModel
import yosel.dev.atti.screens.diagnosis_form.ui.DiagnosisFormEvent
import yosel.dev.atti.screens.diagnosis_form.ui.DiagnosisFormScreen
import yosel.dev.atti.screens.diagnosis_form.ui.DiagnosisFormViewModel
import yosel.dev.atti.screens.main.ui.MainScreen
import yosel.dev.atti.screens.physio_consts_form.ui.PhysioConstsFormEvent
import yosel.dev.atti.screens.physio_consts_form.ui.PhysioConstsFormScreen
import yosel.dev.atti.screens.physio_consts_form.ui.PhysioConstsFormViewModel
import yosel.dev.atti.screens.product_form.ui.ProductFormEvent
import yosel.dev.atti.screens.product_form.ui.ProductFormScreen
import yosel.dev.atti.screens.product_form.ui.ProductFormViewModel
import yosel.dev.atti.screens.service_form.ui.ServiceFormEvent
import yosel.dev.atti.screens.service_form.ui.ServiceFormScreen
import yosel.dev.atti.screens.service_form.ui.ServiceFormViewModel
import yosel.dev.atti.screens.treatment_form.ui.TreatmentFormEvent
import yosel.dev.atti.screens.treatment_form.ui.TreatmentFormScreen
import yosel.dev.atti.screens.treatment_form.ui.TreatmentFormViewModel

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
        val context = LocalContext.current

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
                is ProductFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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

fun EntryProviderScope<NavKey>.detailProductEntry(
    onBack: () -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    entry<Screens.DetailProduct> { detailProductKey ->
        val viewModel: DetailProductViewModel = hiltViewModel(
            creationCallback = { factory: DetailProductViewModel.Factory ->
                factory.create(productId = detailProductKey.productId)
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DetailProductEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is DetailProductEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is DetailProductEvent.OnNavigationMain -> {
                    onNavigationMain(event.screen)
                }
            }
        }

        DetailProductScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackbarHostState,
            onAction = viewModel::onAction,
            onBack = onBack,
            showEditAction = detailProductKey.showEditAction
        )
    }
}

fun EntryProviderScope<NavKey>.serviceFormEntry(
    onBack: () -> Unit,
){
    entry<Screens.ServiceForm> { serviceFormKey ->
        val viewModel: ServiceFormViewModel = hiltViewModel(
            creationCallback = { factory: ServiceFormViewModel.Factory ->
                factory.create(
                    serviceId = serviceFormKey.serviceId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when(event){
                is ServiceFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is ServiceFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                    }
                is ServiceFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        ServiceFormScreen(
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

fun EntryProviderScope<NavKey>.detailServiceEntry(
    onBack: () -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    entry<Screens.DetailService> { detailServiceKey ->
        val viewModel: DetailServiceViewModel = hiltViewModel(
            creationCallback = { factory: DetailServiceViewModel.Factory ->
                factory.create(serviceId = detailServiceKey.serviceId)
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DetailServiceEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is DetailServiceEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is DetailServiceEvent.OnNavigationMain -> {
                    onNavigationMain(event.screen)
                }
            }
        }

        DetailServiceScreen(
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

fun EntryProviderScope<NavKey>.detailConsultationEntry(
    onBack: () -> Unit,
    onNavigationMain: (Screens) -> Unit
){
    entry<Screens.DetailConsultation> { detailConsultationKey ->
        val viewModel: DetailConsultationViewModel = hiltViewModel(
            creationCallback = { factory: DetailConsultationViewModel.Factory ->
                factory.create(consultationId = detailConsultationKey.consultationId)
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DetailConsultationEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
            }
        }

        DetailConsultationScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackbarHostState,
            onBack = onBack,
            onNavigationMain = onNavigationMain
        )
    }
}

fun EntryProviderScope<NavKey>.anamnesisFormEntry(
    onBack: () -> Unit,
){
    entry<Screens.AnamnesisForm> { anamnesisFormKey ->
        val viewModel: AnamnesisFormViewModel = hiltViewModel(
            creationCallback = { factory: AnamnesisFormViewModel.Factory ->
                factory.create(
                    consultationId = anamnesisFormKey.consultationId,
                    anamnesisId = anamnesisFormKey.anamnesisId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when(event){
                is AnamnesisFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is AnamnesisFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is AnamnesisFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        AnamnesisFormScreen(
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

fun EntryProviderScope<NavKey>.emptyEntry(){
    entry<Screens.Empty> {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Text(
                    text = "Esta pantalla está vacía",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

fun EntryProviderScope<NavKey>.clinicalExamFormEntry(
    onBack: () -> Unit
) {
    entry<Screens.ClinicalExamForm> { clinicalExamFormKey ->
        val viewModel: ClinicalExamFormViewModel = hiltViewModel(
            creationCallback = { factory: ClinicalExamFormViewModel.Factory ->
                factory.create(
                    consultationId = clinicalExamFormKey.consultationId,
                    examId = clinicalExamFormKey.examId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is ClinicalExamFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is ClinicalExamFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is ClinicalExamFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        ClinicalExamFormScreen(
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

fun EntryProviderScope<NavKey>.physioConstsFormEntry(
    onBack: () -> Unit
) {
    entry<Screens.PhysioConstsForm> { constsKey ->
        val viewModel: PhysioConstsFormViewModel = hiltViewModel(
            creationCallback = { factory: PhysioConstsFormViewModel.Factory ->
                factory.create(
                    consultationId = constsKey.consultationId,
                    constsId = constsKey.constsId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is PhysioConstsFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is PhysioConstsFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is PhysioConstsFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        PhysioConstsFormScreen(
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

fun EntryProviderScope<NavKey>.diagnosisFormEntry(
    onBack: () -> Unit
) {
    entry<Screens.DiagnosisForm> { key ->
        val viewModel: DiagnosisFormViewModel = hiltViewModel(
            creationCallback = { factory: DiagnosisFormViewModel.Factory ->
                factory.create(
                    consultationId = key.consultationId,
                    diagnosisId = key.diagnosisId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is DiagnosisFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is DiagnosisFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is DiagnosisFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        DiagnosisFormScreen(
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

fun EntryProviderScope<NavKey>.treatmentFormEntry(
    onBack: () -> Unit
) {
    entry<Screens.TreatmentForm> { key ->
        val viewModel: TreatmentFormViewModel = hiltViewModel(
            creationCallback = { factory: TreatmentFormViewModel.Factory ->
                factory.create(
                    consultationId = key.consultationId,
                    treatmentId = key.treatmentId
                )
            }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when (event) {
                is TreatmentFormEvent.ShowErrorSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.ERROR
                        )
                    }
                }
                is TreatmentFormEvent.ShowSuccessSnackbar -> {
                    scope.launch {
                        snackbarHostState.showCustomSnackbar(
                            message = event.message,
                            type = SnackbarType.SUCCESS
                        )
                    }
                }
                is TreatmentFormEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        TreatmentFormScreen(
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