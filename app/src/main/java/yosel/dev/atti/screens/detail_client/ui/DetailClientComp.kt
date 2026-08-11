package yosel.dev.atti.screens.detail_client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import yosel.dev.atti.R
import yosel.dev.atti.core.components.InputFieldWithTextGlobal
import yosel.dev.atti.core.components.StatusChip
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.dialPhoneNumber
import yosel.dev.atti.core.utils.getGenderInfo
import yosel.dev.atti.core.utils.getSpeciesInfo
import yosel.dev.atti.core.utils.openWhatsApp
import yosel.dev.atti.ui.theme.AttiTheme
import yosel.dev.atti.ui.theme.customColors

@Composable
fun BodyDetailClient(
    modifier: Modifier = Modifier,
    state: DetailClientState,
    onAction: (DetailClientAction) -> Unit
) {
    val client = state.clientWithPatients.client
    val patients = state.clientWithPatients.sortedPatients

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            ProfileHeader(client = client)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            ActionButtons(
                onCallClick = { onAction(DetailClientAction.OnCallClick(phoneNumber = client.phoneNumber)) },
                onWhatsappClick = { onAction(DetailClientAction.OnWhatsappClick(phoneNumber = client.phoneNumber)) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            ClientMainInfoCard(client = client)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            ContactInfoCard(client = client)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            PetsSectionHeader(onAddPetClick = { /* Funcionalidad posterior */ })
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (patients.isEmpty()) {
            item {
                EmptyPetsState()
            }
        } else {
            items(patients, key = { it.id }) { patient ->
                DetailPatientCard(
                    patient = patient,
                    onCardClick = { /* Navegación posterior */ }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientBottomSheet(
    state: DetailClientState,
    onAction: (DetailClientAction) -> Unit
) {
    // 1. Iniciar en Hidden permite que Compose realice la animación de entrada al montarse
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // 2. Función helper para realizar la animación de salida antes de destruir el composable
    fun dismissWithAnimation() {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onAction(DetailClientAction.OnDismissEdit)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            // Se ejecuta si el usuario presiona el botón atrás del sistema o toca el scrim exterior
            onAction(DetailClientAction.OnDismissEdit)
        },
        sheetState = sheetState,
        dragHandle = null, // Al quitar el handle evitas arrastres accidentales desde la cabecera
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // Mantiene el respetado de la barra de estado (Hora/WiFi)
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
                    text = "Editar información",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Botón cerrar con salida animada
                IconButton(onClick = { dismissWithAnimation() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario
            val lazyListState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .nestedScroll(rememberNestedScrollInteropConnection()),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    InputFieldWithTextGlobal(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Nombres",
                        placeHolder = "ej. Juan Jose",
                        value = state.editFormState.firstName,
                        onValueChange = {
                            onAction(DetailClientAction.OnChangeEditFormValue(it, Constants.FIRST_NAME_FIELD))
                        },
                        leadingIcon = Icons.Outlined.Person,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.editFormState.isError(Constants.FIRST_NAME_FIELD),
                        errorMessage = "Este campo no puede estar vacío"
                    )
                }
                item {
                    InputFieldWithTextGlobal(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Apellidos",
                        placeHolder = "ej. Perez Hernandez",
                        value = state.editFormState.lastName,
                        onValueChange = {
                            onAction(DetailClientAction.OnChangeEditFormValue(it, Constants.LAST_NAME_FIELD))
                        },
                        leadingIcon = Icons.Outlined.Person,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.editFormState.isError(Constants.LAST_NAME_FIELD),
                        errorMessage = "Este campo no puede estar vacío"
                    )
                }
                item {
                    InputFieldWithTextGlobal(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Nit",
                        placeHolder = "ej. 12345678",
                        value = state.editFormState.documentId,
                        onValueChange = {
                            onAction(DetailClientAction.OnChangeEditFormValue(it, Constants.DOCUMENT_ID_FIELD))
                        },
                        leadingIcon = Icons.Outlined.Badge,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.editFormState.isError(Constants.DOCUMENT_ID_FIELD),
                        errorMessage = "Este campo no puede estar vacío"
                    )
                }
                item {
                    InputFieldWithTextGlobal(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Teléfono",
                        placeHolder = "ej. 87654321",
                        value = state.editFormState.phoneNumber,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("""^\d*$"""))) {
                                onAction(DetailClientAction.OnChangeEditFormValue(it, Constants.PHONE_NUMBER_FIELD))
                            }
                        },
                        leadingIcon = Icons.Outlined.Call,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.editFormState.isError(Constants.PHONE_NUMBER_FIELD),
                        errorMessage = "Este campo no puede estar vacío"
                    )
                }
                item {
                    InputFieldWithTextGlobal(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Dirección",
                        placeHolder = "ej. Palencia",
                        value = state.editFormState.address,
                        onValueChange = {
                            onAction(DetailClientAction.OnChangeEditFormValue(it, Constants.ADDRESS_FIELD))
                        },
                        leadingIcon = Icons.Outlined.Place,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.editFormState.isError(Constants.ADDRESS_FIELD),
                        errorMessage = "Este campo no puede estar vacío"
                    )
                }
                item {
                    InputFieldWithTextGlobal(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Email (Opcional)",
                        placeHolder = "ej. Ejemplo@gmail.com",
                        value = state.editFormState.email,
                        onValueChange = {
                            onAction(DetailClientAction.OnChangeEditFormValue(it, Constants.EMAIL_FIELD))
                        },
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        isError = state.editFormState.isError(Constants.EMAIL_FIELD),
                        errorMessage = null
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    // Si deseas cerrar con animación al presionar guardar:
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onAction(DetailClientAction.OnUpdateClient)
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
                        contentDescription = "guardar cambios"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Guardar cambios")
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(client: ClientModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Imagen de perfil
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.img_client),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                    )
                    // Comentado para uso futuro
                    // AsyncImage(model = client.photoUrl, contentDescription = null, ...)
                }
            }
            
            // Badge Circular Primary
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre (Nombres Apellidos)
        Text(
            text = "${client.firstName} ${client.lastName}".trim().ifBlank { "Sin nombre" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))
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
        FilledTonalButton(
            onClick = onCallClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Llamar", style = MaterialTheme.typography.labelLarge)
        }

        FilledTonalButton(
            onClick = onWhatsappClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.customColors.whatsapp,
                contentColor = MaterialTheme.customColors.onWhatsapp
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

@Composable
private fun ClientMainInfoCard(client: ClientModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InfoRow(
                icon = Icons.Outlined.CalendarToday,
                label = "Miembro desde",
                value = client.formattedCreatedAt.ifBlank { "Sin fecha" }
            )
            
            InfoRow(
                icon = Icons.Outlined.Badge,
                label = "NIT / Documento",
                value = client.documentId.ifBlank { "No registrado" }
            )

            InfoRow(
                icon = Icons.Outlined.Info,
                label = "Estado",
                value = "",
                valueComposable = {
                    StatusChip(status = client.status)
                }
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueComposable: @Composable (() -> Unit)? = null
) {
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
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (valueComposable != null){
                Spacer(modifier = Modifier.height(4.dp))
                valueComposable()
            }else{
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

    }
}

@Composable
private fun ContactInfoCard(client: ClientModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Contacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ContactItem(
                icon = Icons.Outlined.Email,
                label = "Correo Electrónico",
                value = client.email.ifBlank { "No registrado" }
            )

            ContactItem(
                icon = Icons.Outlined.Call,
                label = "Teléfono Móvil",
                value = client.phoneNumber.ifBlank { "No registrado" }
            )

            ContactItem(
                icon = Icons.Outlined.LocationOn,
                label = "Dirección de Residencia",
                value = client.address.ifBlank { "No registrada" }
            )
        }
    }
}

@Composable
private fun ContactItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun PetsSectionHeader(onAddPetClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mis Mascotas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        IconButton(
            onClick = onAddPetClick,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Agregar mascota",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailPatientCard(
    patient: PatientModel,
    onCardClick: () -> Unit
) {
    val speciesInfo = getSpeciesInfo(patient.speciesId)
    val genderInfo = getGenderInfo(patient.genderId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono especie
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = speciesInfo.icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "${speciesInfo.label} • ${patient.breed.ifBlank { "Sin raza" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = genderInfo.icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = genderInfo.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun EmptyPetsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sin mascotas vinculadas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Este cliente aún no tiene mascotas registradas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@PreviewLightDark
@Composable
private fun DetailClientPreview() {
    AttiTheme {
        BodyDetailClient(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
            ,
            state = DetailClientState(
                clientWithPatients = ClientWithPatientsModel(
                    client = ClientModel(
                        id = "20c092a1-b934-446c-a9fc-2b9b44123548",
                        firstName = "Carlos Yosel",
                        lastName = "Alvizures Bran",
                        createdAt = "2026-08-04 20:47:53.952805+00",
                        documentId = "1273390-3",
                        address = " fasdlfhaks dfjksdfh kjasdfh kajsdfh aksdfh akjsdfh askdjfh askj "
                    )
                )
            ),
            onAction = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun CardPatientPreview() {
    AttiTheme {
        DetailPatientCard(
            patient = PatientModel(
                name = "klfklasdjf safklj saklfjlas dfjlksd j asdf asdf asdf asdf asd",
                speciesId = 1,
                breed = "Pastora aleman adf asdfa sdfa sdfas fasdf asdf asdf sadf asdf",
                genderId = 1
            ),
            onCardClick = {}
        )
    }
}
