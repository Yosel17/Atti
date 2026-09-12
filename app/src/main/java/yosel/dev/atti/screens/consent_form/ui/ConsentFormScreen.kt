package yosel.dev.atti.screens.consent_form.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.PermissionRationaleDialog
import yosel.dev.atti.core.components.PermissionSettingsDialog
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.screens.observation_form.ui.ObservationFormAction

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConsentFormScreen(
    modifier: Modifier = Modifier,
    state: ConsentFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (ConsentFormAction) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Consentimiento" else "Consentimiento",
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
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when {
                        targetState.isLoadingDataInitial -> "LOADING"
                        !targetState.isSuccessGetData -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "ConsentFormScreenAnimation"
            ){ targetState ->

                when{
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    !targetState.isSuccessGetData -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la consulta",
                            subTitle = "Ocurrió un error al consultar los datos del paciente. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(ConsentFormAction.TryLoadAgain) }
                        )
                    }
                    else ->{
                        BodyConsentForm(
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

        if (state.isLoadingSaveConsent){
            LoadingDialog(
                title = "Guardando Consentimiento...",
                subtitle = "Estamos registrando el consentimiento en el expediente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.isLoadingUpdateConsent){
            LoadingDialog(
                title = "Actualizando Consentimiento...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.showDialogConfirm){

        }

        if (state.isBottomSheetVisible) {
            SourceSelectionBottomSheet(
                onDismiss = { onAction(UploadBillAction.OnDismissBottomSheet) },
                onSelectCamera = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        onAction(UploadBillAction.OnSelectCameraClick)
                    } else {
                        onAction(UploadBillAction.OnObtainPermits)
                    }
                },
                onSelectGallery = { onAction(UploadBillAction.OnSelectGalleryClick) }
            )
        }

        if (state.showRationaleDialog) {
            PermissionRationaleDialog(
                onDismiss = { onAction(UploadBillAction.OnToggleRationaleDialog(show = false)) },
                onConfirm = {
                    onAction(UploadBillAction.OnToggleRationaleDialog(show = false))
                    onAction(UploadBillAction.OnObtainPermits)
                }
            )
        }

        if (state.showSettingsDialog) {
            PermissionSettingsDialog(
                onDismiss = { onAction(UploadBillAction.OnToggleSettingsDialog(show = false)) },
                onGoToSettings = {
                    onAction(UploadBillAction.OnToggleSettingsDialog(show = false))
                    context.openAppSettings()
                }
            )
        }
    }
}