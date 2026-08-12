package yosel.dev.atti.screens.detail_supplier.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import yosel.dev.atti.R
import yosel.dev.atti.core.components.InputFieldWithTextGlobal
import yosel.dev.atti.core.components.StatusChip
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.AttiTheme
import yosel.dev.atti.ui.theme.customColors

@Composable
fun BodyDetailSupplier(
    modifier: Modifier = Modifier,
    state: DetailSupplierState,
    onAction: (DetailSupplierAction) -> Unit
) {
    val supplier = state.supplier

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Card 1: Nombre, Icono y NIT
        SupplierMainInfoCard(supplier = supplier)

        // Card 2: Información de Contacto + Botones de Acción
        SupplierContactCard(
            supplier = supplier,
            onCallClick = { onAction(DetailSupplierAction.OnCallClick(supplier.phoneNumber)) },
            onWhatsappClick = { onAction(DetailSupplierAction.OnWhatsappClick(supplier.phoneNumber)) }
        )

        // Card 3: Ubicación
        SupplierLocationCard(supplier = supplier)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// Card 1: Nombre, Icono y NIT
// ==========================================
@Composable
private fun SupplierMainInfoCard(
    supplier: SupplierModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular del proveedor
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = supplier.name.ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusChip(status = supplier.status)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Fila para el NIT
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "NIT",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = supplier.taxId.ifBlank { "No registrado" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==========================================
// Card 2: Contacto y Botones
// ==========================================
@Composable
private fun SupplierContactCard(
    supplier: SupplierModel,
    onCallClick: () -> Unit,
    onWhatsappClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información de Contacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Fila Teléfono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Teléfono",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = supplier.phoneNumber.ifBlank { "No registrado" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // ActionButtons de Llamar y WhatsApp reutilizando la estética de detail_client
            ActionButtons(
                onCallClick = onCallClick,
                onWhatsappClick = onWhatsappClick
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onCallClick: () -> Unit,
    onWhatsappClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCallClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Llamar", style = MaterialTheme.typography.labelLarge)
        }

        OutlinedButton(
            onClick = onWhatsappClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.customColors.whatsapp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.whatsapp),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "WhatsApp", style = MaterialTheme.typography.labelLarge)
        }
    }
}

// ==========================================
// Card 3: Ubicación
// ==========================================
@Composable
private fun SupplierLocationCard(
    supplier: SupplierModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ubicación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Dirección",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = supplier.address.ifBlank { "No registrada" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==========================================
// BottomSheet de Edición de Proveedor
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSupplierBottomSheet(
    state: DetailSupplierState,
    onAction: (DetailSupplierAction) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    fun dismissWithAnimation() {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onAction(DetailSupplierAction.OnDismissEdit)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onAction(DetailSupplierAction.OnDismissEdit)
        },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Editar proveedor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { dismissWithAnimation() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario de edición reutilizando inputs y validaciones de add_supplier
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                InputFieldWithTextGlobal(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Nombre del proveedor",
                    placeHolder = "ej. Distribuidora San Carlos",
                    value = state.editFormState.name,
                    onValueChange = {
                        onAction(
                            DetailSupplierAction.OnChangeEditFormValue(
                                it,
                                Constants.SUPPLIER_NAME_FIELD
                            )
                        )
                    },
                    leadingIcon = Icons.Outlined.Business,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.editFormState.isError(Constants.SUPPLIER_NAME_FIELD),
                    errorMessage = "Este campo no puede estar vacío"
                )

                InputFieldWithTextGlobal(
                    modifier = Modifier.fillMaxWidth(),
                    label = "NIT del proveedor",
                    placeHolder = "ej. 1234567-8",
                    value = state.editFormState.taxId,
                    onValueChange = {
                        onAction(
                            DetailSupplierAction.OnChangeEditFormValue(
                                it,
                                Constants.SUPPLIER_TAX_ID_FIELD
                            )
                        )
                    },
                    leadingIcon = Icons.Outlined.Badge,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.editFormState.isError(Constants.SUPPLIER_TAX_ID_FIELD),
                    errorMessage = "Este campo no puede estar vacío"
                )

                InputFieldWithTextGlobal(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Teléfono de contacto",
                    placeHolder = "ej. 55554444",
                    value = state.editFormState.phoneNumber,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*$"""))) {
                            onAction(
                                DetailSupplierAction.OnChangeEditFormValue(
                                    newValue,
                                    Constants.SUPPLIER_PHONE_FIELD
                                )
                            )
                        }
                    },
                    leadingIcon = Icons.Outlined.Phone,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.editFormState.isError(Constants.SUPPLIER_PHONE_FIELD),
                    errorMessage = "Este campo no puede estar vacío"
                )

                InputFieldWithTextGlobal(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Dirección completa",
                    placeHolder = "ej. Calle Principal Z.1, Palencia",
                    value = state.editFormState.address,
                    onValueChange = {
                        onAction(
                            DetailSupplierAction.OnChangeEditFormValue(
                                it,
                                Constants.SUPPLIER_ADDRESS_FIELD
                            )
                        )
                    },
                    leadingIcon = Icons.Outlined.Place,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.editFormState.isError(Constants.SUPPLIER_ADDRESS_FIELD),
                    errorMessage = "Este campo no puede estar vacío"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onAction(DetailSupplierAction.OnUpdateSupplier)
                    }
                },
                enabled = state.editFormState.isValid && state.editFormState.hasChangesFrom(state.initialEditFormState)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = "Guardar cambios"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Guardar cambios")
                }
            }
        }
    }
}

// ==========================================
// Diálogo de advertencia cuando el proveedor está eliminado
// ==========================================
@Composable
fun DialogInformativeSupplierEdition(
    modifier: Modifier = Modifier,
    name: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "El proveedor $name se encuentra eliminado",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Este proveedor se encuentra eliminado y su información no se puede modificar. Restablécelo para poder editarlo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = onDismiss
                ) {
                    Text("Entiendo")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BodyDetailPreview() {
    AttiTheme {
        BodyDetailSupplier(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
            state = DetailSupplierState(
                isLoading = false,
                supplier = SupplierModel(
                    id = "asdfnjkasdfhjkf",
                    name = "Distribuidora la bendicion",
                    taxId = "95997725k",
                    phoneNumber = "30915902",
                    address = "4 av. 0-60 canton agua tibia palencia"
                )
            ),
            onAction = {}
        )
    }
}