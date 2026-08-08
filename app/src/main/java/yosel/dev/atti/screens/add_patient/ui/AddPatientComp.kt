package yosel.dev.atti.screens.add_patient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.getGenderInfo
import yosel.dev.atti.core.utils.getSpeciesInfo
import yosel.dev.atti.ui.theme.AttiTheme

@Composable
fun BodyAddPatient(
    modifier: Modifier = Modifier,
    state: AddPatientState,
    onAction: (AddPatientAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                HeaderSection(speciesId = state.formState.speciesId)
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }

            item {
                SectionTitle(
                    title = "Datos Básicos",
                    icon = Icons.Filled.Info
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                InputFieldGlobal(
                    label = "Nombre de la mascota",
                    placeholder = "Ej: Max",
                    value = state.formState.name,
                    onValueChange = { onAction(AddPatientAction.OnChangeValueFormState(it, Constants.PATIENT_NAME_FIELD)) },
                    leadingIcon = Icons.Outlined.Pets,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.formState.isError(Constants.PATIENT_NAME_FIELD),
                    errorMessage = "El nombre es obligatorio"
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CatalogSelection(
                    label = "Especie",
                    catalogs = state.speciesCatalog,
                    selectedId = state.formState.speciesId,
                    onSelect = { onAction(AddPatientAction.OnSelectSpecies(it)) },
                    isSpecies = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                InputFieldGlobal(
                    label = "Raza",
                    placeholder = "Ej: Golden Retriever",
                    value = state.formState.breed,
                    onValueChange = { onAction(AddPatientAction.OnChangeValueFormState(it, Constants.PATIENT_BREED_FIELD)) },
                    leadingIcon = Icons.Outlined.Fingerprint,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.formState.isError(Constants.PATIENT_BREED_FIELD),
                    errorMessage = "La raza es obligatoria"
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CatalogSelection(
                    label = "Género",
                    catalogs = state.genderCatalog,
                    selectedId = state.formState.genderId,
                    onSelect = { onAction(AddPatientAction.OnSelectGender(it)) },
                    isSpecies = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }

            item {
                SectionTitle(
                    title = "Detalles",
                    icon = Icons.AutoMirrored.Filled.Assignment
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InputFieldGlobal(
                        modifier = Modifier.weight(1f),
                        label = "Años",
                        placeholder = "0",
                        value = state.formState.ageYears,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                onAction(AddPatientAction.OnChangeValueFormState(it, Constants.PATIENT_AGE_YEARS_FIELD))
                            }
                        },
                        leadingIcon = Icons.AutoMirrored.Outlined.Assignment,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.formState.isError(Constants.PATIENT_AGE_YEARS_FIELD),
                        errorMessage = "Requerido"
                    )

                    InputFieldGlobal(
                        modifier = Modifier.weight(1f),
                        label = "Meses",
                        placeholder = "0",
                        value = state.formState.ageMonths,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                onAction(AddPatientAction.OnChangeValueFormState(it, Constants.PATIENT_AGE_MONTHS_FIELD))
                            }
                        },
                        leadingIcon = Icons.AutoMirrored.Outlined.Assignment,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isError = state.formState.isError(Constants.PATIENT_AGE_MONTHS_FIELD),
                        errorMessage = "Requerido"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                InputFieldGlobal(
                    label = "Color",
                    placeholder = "Ej: Canela y Blanco",
                    value = state.formState.color,
                    onValueChange = { onAction(AddPatientAction.OnChangeValueFormState(it, Constants.PATIENT_COLOR_FIELD)) },
                    leadingIcon = Icons.Outlined.ColorLens,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.formState.isError(Constants.PATIENT_COLOR_FIELD),
                    errorMessage = "El color es obligatorio"
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                NeuteredSelection(
                    isNeutered = state.formState.isNeutered,
                    onToggle = { onAction(AddPatientAction.OnToggleNeutered(it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }

            item {
                SectionTitle(
                    title = "Propietario",
                    icon = Icons.Filled.Person
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ClientSelector(
                    clients = state.clients,
                    selectedClient = state.formState.selectedClient,
                    onSelect = { onAction(AddPatientAction.OnSelectClient(it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(AddPatientAction.RegisterPatient)
            },
            enabled = state.formState.isValid,
            shape = RoundedCornerShape(100.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Assignment,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Registrar paciente",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HeaderSection(speciesId: Int) {
    val speciesInfo = getSpeciesInfo(speciesId)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = speciesInfo.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Completa el perfil del nuevo paciente",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp) // Un poco más grande que el diseño
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CatalogSelection(
    label: String,
    catalogs: List<AppCatalogModel>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    isSpecies: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            catalogs.forEach { catalog ->
                val isSelected = catalog.id == selectedId
                val info = if (isSpecies) getSpeciesInfo(catalog.id) else null
                val genderInfo = if (!isSpecies) getGenderInfo(catalog.id) else null

                CatalogChip(
                    text = catalog.name,
                    icon = info?.icon,
                    iconVector = genderInfo?.icon,
                    isSelected = isSelected,
                    onClick = { onSelect(catalog.id) }
                )
            }

            CatalogChip(
                text = "Otros",
                icon = if (isSpecies) getSpeciesInfo(0).icon else null,
                iconVector = if (!isSpecies) getGenderInfo(0).icon else null,
                isSelected = selectedId == -1,
                onClick = { /* TODO: Agregar funcionalidad de otros */ }
            )
        }
    }
}

@Composable
fun CatalogChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Int? = null,
    iconVector: ImageVector? = null,
) {
    val leadingIconContent: @Composable (() -> Unit)? = when {
        icon != null -> {
            {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        iconVector != null -> {
            {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        else -> null
    }

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = leadingIconContent,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun NeuteredSelection(isNeutered: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Está castrado?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Operación realizada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isNeutered,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun ClientSelector(
    clients: List<ClientModel>,
    selectedClient: ClientModel?,
    onSelect: (ClientModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(selectedClient?.fullName ?: "") }

    val filteredClients = remember(searchQuery, clients) {
        if (searchQuery.isBlank()) clients
        else clients.filter { it.fullName.contains(searchQuery, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        InputFieldGlobal(
            label = "Vincular a un Cliente",
            placeholder = "Buscar cliente existente...",
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                expanded = true
            },
            leadingIcon = Icons.Outlined.Person,
            readOnly = false
        )

        DropdownMenu(
            expanded = expanded && filteredClients.isNotEmpty(),
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = false),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            filteredClients.forEach { client ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = if (client.id == selectedClient?.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = client.fullName,
                                fontWeight = if (client.id == selectedClient?.id) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = {
                        onSelect(client)
                        searchQuery = client.fullName
                        expanded = false
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BodyPreview() {
    AttiTheme {
        BodyAddPatient(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            state = AddPatientState(
                isLoadingDataInitial = false,
                speciesCatalog = listOf(
                    AppCatalogModel(
                        id = 1,
                        name = "Canino"
                    )
                ),
                genderCatalog = listOf(
                    AppCatalogModel(
                        id = 4,
                        name = "Macho"
                    )
                ),
                clients = listOf(
                    ClientModel(
                        id = "adfjlkadfj-adfnkladfjn-afshdfa",
                        firstName = "Carlos Yosel",
                        lastName = "Alvizures Bran"
                    )
                )
            ),
            onAction = {}
        )
    }
}
