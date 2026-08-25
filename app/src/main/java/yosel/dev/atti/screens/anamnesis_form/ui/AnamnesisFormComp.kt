package yosel.dev.atti.screens.anamnesis_form.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import yosel.dev.atti.core.components.AppCatalogMultiSelector
import yosel.dev.atti.core.components.AppCatalogSelector
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisDewormingWithDetailsModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineWithDetailsModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.AttiTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun BodyAnamnesisForm(
    modifier: Modifier = Modifier,
    state: AnamnesisFormState,
    onAction: (AnamnesisFormAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            EnvironmentAndRoutineSection(
                state = state,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProphylaxisSection(
                state = state,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(24.dp))
            HousematesSection(
                formInputState = state.formInputState,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(24.dp))
            FeedingSection(
                state = state,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(AnamnesisFormAction.SaveAnamnesis)
            },
            shape = RoundedCornerShape(100.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddBox,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar Anamnesis",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EnvironmentAndRoutineSection(
    state: AnamnesisFormState,
    onAction: (AnamnesisFormAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SectionHeader(
                icon = Icons.Default.Grass,
                title = "Entorno y rutina"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "¿Tiene acceso al exterior o sale a pasear?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = state.formInputState.hasOutdoorAccess,
                    onCheckedChange = { onAction(AnamnesisFormAction.OnOutdoorAccessChange(it)) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Seleccione las opciones que correspondan:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppCatalogMultiSelector(
                selectedCatalogs = state.formInputState.selectedEnvironmentOptions,
                onOpenSheet = { onAction(AnamnesisFormAction.OnOpenEnvironmentOptionsSheet) },
                onRemoveCatalog = { onAction(AnamnesisFormAction.OnRemoveEnvironmentOption(it)) },
                icon = Icons.Default.Grass,
                emptyText = "Seleccionar opciones de entorno"
            )
        }
    }
}

@Composable
private fun ProphylaxisSection(
    state: AnamnesisFormState,
    onAction: (AnamnesisFormAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SectionHeader(
                icon = Icons.Default.HealthAndSafety,
                title = "Profilaxis"
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Subsección Vacunas
            Text(
                text = "Vacunas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (state.formInputState.vaccines.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay vacunas registradas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = { onAction(AnamnesisFormAction.OnOpenAddVaccineSheet) },
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Agregar vacuna")
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.formInputState.vaccines.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Vaccines,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.vaccine.name.ifBlank { "Vacuna no especificada" },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (item.scheme.name.isNotBlank()) {
                                        Text(
                                            text = item.scheme.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (item.vaccineEntry.applicationDate.isNotBlank()) {
                                        Text(
                                            text = item.vaccineEntry.applicationDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                IconButton(onClick = { onAction(AnamnesisFormAction.OnDeleteVaccine(item)) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction(AnamnesisFormAction.OnOpenAddVaccineSheet) },
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Agregar otra vacuna")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subsección Desparasitantes
            Text(
                text = "Desparasitantes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (state.formInputState.dewormings.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay desparasitantes registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = { onAction(AnamnesisFormAction.OnOpenAddDewormingSheet) },
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Agregar desparasitante")
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.formInputState.dewormings.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.name.ifBlank { "Producto no especificado" },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.deworming.dewormingType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (item.deworming.applicationDate.isNotBlank()) {
                                        Text(
                                            text = item.deworming.applicationDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                IconButton(onClick = { onAction(AnamnesisFormAction.OnDeleteDeworming(item)) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction(AnamnesisFormAction.OnOpenAddDewormingSheet) },
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Agregar otro desparasitante")
                    }
                }
            }
        }
    }
}

@Composable
private fun HousematesSection(
    formInputState: AnamnesisFormInputsState,
    onAction: (AnamnesisFormAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SectionHeader(
                icon = Icons.Default.Pets,
                title = "Compañeros en casa"
            )
            Spacer(modifier = Modifier.height(20.dp))
            InputFieldGlobal(
                label = "Compañeros en casa",
                placeholder = "Ej: 2 perros y 1 gato, todos con esquema de vacunación completo.",
                value = formInputState.housemates,
                onValueChange = { onAction(AnamnesisFormAction.OnHousematesChange(it)) },
                leadingIcon = Icons.Default.Pets,
                singleLine = false,
                minLines = 1,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedingSection(
    state: AnamnesisFormState,
    onAction: (AnamnesisFormAction) -> Unit
) {
    val feedingFrequencies = listOf(
        "1 vez al día",
        "2 veces al día",
        "3 veces al día",
        "4 veces al día",
        "5 veces al día",
        "6 veces al día",
        "7 veces al día"
    )
    val waterOptions = listOf("Aumentado", "Disminuido", "Normal", "No iden.")
    var isFrequencyDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SectionHeader(
                icon = Icons.Default.Restaurant,
                title = "Alimentación"
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Marca de concentrado
            SectionTitle(title = "Marca de concentrado", icon = Icons.Default.Restaurant, showIcon = false)
            Spacer(modifier = Modifier.height(12.dp))
            AppCatalogSelector(
                selectedCatalog = state.formInputState.selectedFoodBrand,
                onOpenSheet = { onAction(AnamnesisFormAction.OnOpenConcentrateBrandSheet) },
                icon = Icons.Default.Restaurant,
                emptyText = "Buscar marca..."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Cantidad de comida (Unidad primero, luego input cantidad)
            SectionTitle(title = "Unidad de medida", icon = Icons.Default.Straighten, showIcon = false)
            Spacer(modifier = Modifier.height(12.dp))
            AppCatalogSelector(
                selectedCatalog = state.formInputState.selectedFoodUnit,
                onOpenSheet = { onAction(AnamnesisFormAction.OnOpenConcentrateUnitSheet) },
                icon = Icons.Default.Straighten,
                emptyText = "Selecciona la unidad de medida"
            )
            Spacer(modifier = Modifier.height(16.dp))
            InputFieldGlobal(
                label = "Cantidad de comida",
                placeholder = "0.0",
                value = state.formInputState.foodQuantity,
                onValueChange = { input ->
                    val sanitized = input.replace(',', '.')
                    if (sanitized.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                        onAction(AnamnesisFormAction.OnFoodQuantityChange(sanitized))
                    }
                },
                leadingIcon = Icons.Default.Numbers,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Comida casera
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Comida casera",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (state.formInputState.hasHomemadeFood) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.clickable { onAction(AnamnesisFormAction.OnHomemadeFoodToggle(true)) }
                        ) {
                            Text(
                                text = "SÍ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (state.formInputState.hasHomemadeFood) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = if (!state.formInputState.hasHomemadeFood) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.clickable { onAction(AnamnesisFormAction.OnHomemadeFoodToggle(false)) }
                        ) {
                            Text(
                                text = "NO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (!state.formInputState.hasHomemadeFood) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(state.formInputState.hasHomemadeFood) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    InputFieldGlobal(
                        label = "Detalles de comida casera",
                        placeholder = "Ej: Caldo de pollo con verduras, arroz...",
                        value = state.formInputState.homemadeFoodDetails,
                        onValueChange = { onAction(AnamnesisFormAction.OnHomemadeFoodDetailsChange(it)) },
                        leadingIcon = Icons.Default.Fastfood,
                        singleLine = false,
                        minLines = 1,
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Tiempos de comida (Dropdown con 7 opciones fijas)
            SectionTitle(title = "Tiempos de comida", icon = Icons.Default.Restaurant, showIcon = false)
            Spacer(modifier = Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = isFrequencyDropdownExpanded,
                onExpandedChange = { isFrequencyDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.formInputState.feedingFrequency,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFrequencyDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
                ExposedDropdownMenu(
                    expanded = isFrequencyDropdownExpanded,
                    onDismissRequest = { isFrequencyDropdownExpanded = false }
                ) {
                    feedingFrequencies.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                onAction(AnamnesisFormAction.OnFeedingFrequencyChange(option))
                                isFrequencyDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Consumo de agua (4 opciones seleccionables)
            SectionTitle(title = "Consumo de agua", icon = Icons.Default.LocalDrink, showIcon = false)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    waterOptions.forEach { option ->
                        val isSelected = state.formInputState.waterConsumption == option
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAction(AnamnesisFormAction.OnWaterConsumptionChange(option)) }
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaccineBottomSheet(
    state: AnamnesisFormState,
    onDismiss: () -> Unit,
    onAction: (AnamnesisFormAction) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val coroutineScope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    fun dismissWithAnimation() {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val (isoDate, displayDate, elapsedText) = calculateDateDetails(millis)
                        onAction(AnamnesisFormAction.OnVaccineDateChange(isoDate, displayDate, elapsedText))
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    text = "Nueva vacuna",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { dismissWithAnimation() }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Cerrar", modifier = Modifier.clip(CircleShape))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Fecha de aplicación
                SectionTitle(title = "Fecha de última aplicación", icon = Icons.Default.CalendarMonth, showIcon = false)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.tempVaccineDisplayDate.ifBlank { "Selecciona la fecha" },
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    interactionSource = interactionSource,
                )
                if (state.tempVaccineElapsedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = state.tempVaccineElapsedText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Producto / Vacuna
                SectionTitle(title = "Producto / Vacuna", icon = Icons.Default.Vaccines, showIcon = false)
                Spacer(modifier = Modifier.height(8.dp))
                AppCatalogSelector(
                    selectedCatalog = state.tempSelectedVaccineCatalog,
                    onOpenSheet = { onAction(AnamnesisFormAction.OnOpenVaccineNameSheet) },
                    icon = Icons.Default.Vaccines,
                    emptyText = "Ej: Nexgard Spectra"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Esquema Canino
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Esquema Canino:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = {
                            onAction(
                                AnamnesisFormAction.OnShowAddCatalogDialog(
                                    catalogTypeId = Constants.VACCINATION_SCHEDULE_TYPE_CATALOG,
                                    catalogTypeName = "Esquema de vacunación"
                                )
                            )
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Agregar esquema")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.vaccinationSchedules.forEach { schedule ->
                        val isSelected = state.tempSelectedScheduleCatalog?.id == schedule.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAction(AnamnesisFormAction.OnSelectVaccineSchedule(schedule)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                else
                                    MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    if (isSelected) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = schedule.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAction(AnamnesisFormAction.OnSaveVaccineEntry) },
                enabled = state.tempSelectedVaccineCatalog != null && state.tempVaccineIsoDate.isNotBlank(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = "Guardar vacuna",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDewormingBottomSheet(
    state: AnamnesisFormState,
    onDismiss: () -> Unit,
    onAction: (AnamnesisFormAction) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val coroutineScope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    fun dismissWithAnimation() {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val (isoDate, displayDate, elapsedText) = calculateDateDetails(millis)
                        onAction(AnamnesisFormAction.OnDewormingDateChange(isoDate, displayDate, elapsedText))
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    text = "Nuevo desparasitante",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { dismissWithAnimation() }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Cerrar", modifier = Modifier.clip(CircleShape))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Fecha de aplicación
                SectionTitle(title = "Fecha de aplicación", icon = Icons.Default.CalendarMonth, showIcon = false)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.tempDewormingDisplayDate.ifBlank { "dd/mm/aaaa" },
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    interactionSource = interactionSource,
                )
                if (state.tempDewormingElapsedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = state.tempDewormingElapsedText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Selector Tipo (Interno / Externo)
                SectionTitle(title = "Tipo", icon = Icons.Default.Rule, showIcon = false)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf("Interno", "Externo")
                        types.forEach { type ->
                            val isSelected = state.tempDewormingType == type
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onAction(AnamnesisFormAction.OnDewormingTypeChange(type)) }
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Selector de Producto según tipo
                SectionTitle(title = "Producto:", icon = Icons.Default.MedicalServices, showIcon = false)
                Spacer(modifier = Modifier.height(8.dp))
                AppCatalogSelector(
                    selectedCatalog = state.tempSelectedDewormerProduct,
                    onOpenSheet = { onAction(AnamnesisFormAction.OnOpenDewormingProductSheet) },
                    icon = Icons.Default.MedicalServices,
                    emptyText = "Selecciona un desparasitante"
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAction(AnamnesisFormAction.OnSaveDewormingEntry) },
                enabled = state.tempSelectedDewormerProduct != null && state.tempDewormingIsoDate.isNotBlank(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = "Guardar desparasitante",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@PreviewLightDark
@Composable
private fun EnvironmentAndRoutineSectionPreview() {
    AttiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            EnvironmentAndRoutineSection(
                state = AnamnesisFormState(
                    formInputState = AnamnesisFormInputsState(
                        hasOutdoorAccess = true,
                        selectedEnvironmentOptions = listOf(
                            AppCatalogModel(id = 1, name = "Tiempos de paseo"),
                            AppCatalogModel(id = 2, name = "Exposición a otros perros"),
                            AppCatalogModel(id = 3, name = "Terraza")
                        )
                    )
                ),
                onAction = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ProphylaxisSectionPreview() {
    AttiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            ProphylaxisSection(
                state = AnamnesisFormState(
                    formInputState = AnamnesisFormInputsState(
                        vaccines = listOf(
                            AnamnesisVaccineWithDetailsModel(
                                vaccineEntry = AnamnesisVaccineModel(
                                    applicationDate = "2026-05-15",
                                    vaccineCatalogId = 1,
                                    schemeCatalogId = 1
                                ),
                                vaccine = AppCatalogModel(id = 1, name = "Rabia"),
                                scheme = AppCatalogModel(id = 1, name = "Esquema completo")
                            ),
                            AnamnesisVaccineWithDetailsModel(
                                vaccineEntry = AnamnesisVaccineModel(
                                    applicationDate = "2026-06-20",
                                    vaccineCatalogId = 2,
                                    schemeCatalogId = 2
                                ),
                                vaccine = AppCatalogModel(id = 2, name = "Séxtuple canina"),
                                scheme = AppCatalogModel(id = 2, name = "Refuerzo anual")
                            )
                        ),
                        dewormings = listOf(
                            AnamnesisDewormingWithDetailsModel(
                                deworming = AnamnesisDewormingModel(
                                    applicationDate = "2026-07-01",
                                    dewormingType = "Interno",
                                    productCatalogId = 1
                                ),
                                product = AppCatalogModel(id = 1, name = "Drontal Plus")
                            ),
                            AnamnesisDewormingWithDetailsModel(
                                deworming = AnamnesisDewormingModel(
                                    applicationDate = "2026-08-10",
                                    dewormingType = "Externo",
                                    productCatalogId = 2
                                ),
                                product = AppCatalogModel(id = 2, name = "NexGard Spectra")
                            )
                        )
                    )
                ),
                onAction = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HousematesSectionPreview() {
    AttiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            HousematesSection(
                formInputState = AnamnesisFormInputsState(
                    housemates = "2 perros y 1 gato, todos con esquema de vacunación al día."
                ),
                onAction = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FeedingSectionPreview() {
    AttiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            FeedingSection(
                state = AnamnesisFormState(
                    formInputState = AnamnesisFormInputsState(
                        selectedFoodBrand = AppCatalogModel(
                            id = 1,
                            name = "Royal Canin Maxi Adult"
                        ),
                        selectedFoodUnit = AppCatalogModel(id = 1, name = "Tazas"),
                        foodQuantity = "2.5",
                        hasHomemadeFood = true,
                        homemadeFoodDetails = "Pollo cocido desmenuzado sin sal y zanahorias.",
                        feedingFrequency = "2 veces al día",
                        waterConsumption = "Normal"
                    )
                ),
                onAction = {}
            )
        }
    }
}

private fun calculateDateDetails(millis: Long): Triple<String, String, String> {
    val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()

    val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val isoDate = isoFormatter.format(Date(millis))

    val displayFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("es-ES"))
    val displayDate = displayFormatter.format(Date(millis))

    val period = Period.between(selectedDate, today)
    val elapsedText = when {
        period.isNegative -> "Fecha futura"
        period.years > 0 && period.months > 0 -> "Hace ${period.years} años, ${period.months} meses"
        period.years > 0 -> "Hace ${period.years} años"
        period.months > 0 && period.days > 0 -> "Hace ${period.months} meses, ${period.days} días"
        period.months > 0 -> "Hace ${period.months} meses"
        period.days > 0 -> "Hace ${period.days} días"
        else -> "Hoy"
    }

    return Triple(isoDate, displayDate, elapsedText)
}