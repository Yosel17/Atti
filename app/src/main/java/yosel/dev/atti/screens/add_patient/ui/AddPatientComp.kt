package yosel.dev.atti.screens.add_patient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.NoSearchResultsState
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
                ClientSelectorSection(
                    selectedClient = state.formState.selectedClient,
                    onOpenSheet = { onAction(AddPatientAction.OnOpenClientSheet) }
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
fun ClientSelectorSection(
    selectedClient: ClientModel?,
    onOpenSheet: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (selectedClient != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = if (selectedClient != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = selectedClient?.fullName ?: "Ningún cliente seleccionado",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedClient != null) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedClient != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onOpenSheet,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (selectedClient == null) "Vincular" else "Cambiar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectClientBottomSheet(
    state: AddPatientState,
    onAction: (AddPatientAction) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val coroutineScope = rememberCoroutineScope()

    fun dismissWithAnimation(onComplete: (() -> Unit)? = null) {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onAction(AddPatientAction.OnDismissClientSheet)
                onComplete?.invoke()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onAction(AddPatientAction.OnDismissClientSheet)
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
                    text = "Seleccionar Cliente",
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

            AttiSearchBar(
                value = state.clientSearchQuery,
                onValueChange = { onAction(AddPatientAction.OnSearchClientQueryChange(it)) },
                placeholder = "Buscar por nombre o apellido..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.filteredClients.isEmpty()){
                NoSearchResultsState(
                    query = state.clientSearchQuery,
                    onClearSearch = { onAction(AddPatientAction.OnSearchClientQueryChange("")) },
                    nameResult = "clientes"
                )
            }else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(rememberNestedScrollInteropConnection()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.filteredClients, key = { it.id }) { client ->
                        val isSelected = client.id == state.formState.selectedClient?.id

                        ClientSelectionCard(
                            client = client,
                            isSelected = isSelected,
                            onClick = {
                                dismissWithAnimation {
                                    onAction(AddPatientAction.OnSelectClient(client))
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun ClientSelectionCard(
    client: ClientModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.fullName.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (client.phoneNumber.isNotBlank()) {
                    Text(
                        text = "Tel. ${client.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
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
