package yosel.dev.atti.screens.physio_consts_form.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import yosel.dev.atti.core.components.AppCatalogSelector
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.PatientConsultationHeaderHero
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.customColors

enum class RangeStatus {
    LOW, NORMAL, HIGH, FEVER, DEFAULT
}

data class RangeEvaluation(
    val statusText: String,
    val normalRangeDescription: String,
    val status: RangeStatus
)

@Composable
fun BodyPhysiologicalConstsForm(
    modifier: Modifier = Modifier,
    state: PhysioConstsFormState,
    onAction: (PhysioConstsFormAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isButtonEnabled = if (state.isEditMode) {
        state.formInputState.hasChangesFrom(state.initialFormInputState)
    } else {
        true
    }
    val speciesId = state.consultationWithDetails.patientWithDetails.patient.speciesId

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            PatientConsultationHeaderHero(
                patientWithDetails = state.consultationWithDetails.patientWithDetails
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Temperatura
            TemperatureCard(
                value = state.formInputState.temperature,
                speciesId = speciesId,
                onValueChange = { onAction(PhysioConstsFormAction.OnTemperatureChange(it)) },
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
            Spacer(modifier = Modifier.height(18.dp))

            // 2. Frecuencia Cardíaca
            HeartRateCard(
                value = state.formInputState.heartRate,
                speciesId = speciesId,
                onValueChange = { onAction(PhysioConstsFormAction.OnHeartRateChange(it)) },
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
            Spacer(modifier = Modifier.height(18.dp))

            // 3. Frecuencia Respiratoria
            RespiratoryRateCard(
                value = state.formInputState.respiratoryRate,
                speciesId = speciesId,
                onValueChange = { onAction(PhysioConstsFormAction.OnRespiratoryRateChange(it)) },
                onDone = { focusManager.clearFocus() }
            )
            Spacer(modifier = Modifier.height(18.dp))

            // 4. Peso
            WeightSectionCard(
                state = state,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(18.dp))

            // 5. TLC (Tiempo de Llenado Capilar)
            CapillaryRefillTimeCard(
                selectedTime = state.formInputState.capillaryRefillTime,
                onSelect = { onAction(PhysioConstsFormAction.OnCapillaryRefillTimeChange(it)) }
            )
            Spacer(modifier = Modifier.height(18.dp))

            // 6. Turgencia de Piel
            SkinTurgorCard(
                selectedTime = state.formInputState.skinTurgor,
                onSelect = { onAction(PhysioConstsFormAction.OnSkinTurgorChange(it)) }
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(PhysioConstsFormAction.ToggleSaveDialog(show = true))
            },
            enabled = isButtonEnabled,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (state.isEditMode) Icons.Outlined.Save else Icons.Default.AddBox,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isEditMode) "Guardar edición" else "Guardar Constantes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// -------------------------------------------------------------
// Componentes Fisiológicos Individuales
// -------------------------------------------------------------

@Composable
private fun TemperatureCard(
    value: String,
    speciesId: Int,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val evaluation = evaluateTemperature(value, speciesId)
    val (badgeContainer, badgeContent) = getRangeColors(evaluation.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeviceThermostat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Temperatura (°C)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (evaluation.statusText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = badgeContainer
                    ) {
                        Text(
                            text = evaluation.statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeContent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SelectableNumberInputField(
                value = value,
                onValueChange = onValueChange,
                allowDecimals = true,
                maxDecimals = 2,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                onAction = onNext
            )

            if (evaluation.normalRangeDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = evaluation.normalRangeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun HeartRateCard(
    value: String,
    speciesId: Int,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val evaluation = evaluateHeartRate(value, speciesId)
    val (badgeContainer, badgeContent) = getRangeColors(evaluation.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Frecuencia Cardíaca (LPM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (evaluation.statusText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = badgeContainer
                    ) {
                        Text(
                            text = evaluation.statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeContent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SelectableNumberInputField(
                value = value,
                onValueChange = onValueChange,
                allowDecimals = false,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                onAction = onNext
            )

            if (evaluation.normalRangeDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = evaluation.normalRangeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun RespiratoryRateCard(
    value: String,
    speciesId: Int,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val evaluation = evaluateRespiratoryRate(value, speciesId)
    val (badgeContainer, badgeContent) = getRangeColors(evaluation.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Frecuencia Respiratoria (RPM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (evaluation.statusText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = badgeContainer
                    ) {
                        Text(
                            text = evaluation.statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeContent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SelectableNumberInputField(
                value = value,
                onValueChange = onValueChange,
                allowDecimals = false,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onAction = onDone
            )

            if (evaluation.normalRangeDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = evaluation.normalRangeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun WeightSectionCard(
    state: PhysioConstsFormState,
    onAction: (PhysioConstsFormAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Scale,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Peso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(title = "Unidad de medida", icon = Icons.Default.Straighten, showIcon = false)
            Spacer(modifier = Modifier.height(8.dp))
            AppCatalogSelector(
                selectedCatalog = state.formInputState.selectedWeightUnit,
                onOpenSheet = { onAction(PhysioConstsFormAction.OnOpenWeightUnitSheet) },
                icon = Icons.Default.Straighten,
                emptyText = "Selecciona la unidad de peso"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFieldGlobal(
                label = "Cantidad de peso",
                placeholder = "0.0",
                value = state.formInputState.weight,
                onValueChange = { input ->
                    val sanitized = input.replace(',', '.')
                    if (sanitized.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                        onAction(PhysioConstsFormAction.OnWeightChange(sanitized))
                    }
                },
                leadingIcon = Icons.Default.Scale,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}

@Composable
private fun CapillaryRefillTimeCard(
    selectedTime: Int,
    onSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TLC (Tiempo de Llenado Capilar)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OptionSelectionButton(
                    modifier = Modifier.weight(1f),
                    title = "2 segundos\n(Normal)",
                    isSelected = selectedTime == 2,
                    onClick = { onSelect(2) }
                )
                OptionSelectionButton(
                    modifier = Modifier.weight(1f),
                    title = "> 3 segundos\n(Anormal)",
                    isSelected = selectedTime == 3,
                    onClick = { onSelect(3) }
                )
            }
        }
    }
}

@Composable
private fun SkinTurgorCard(
    selectedTime: Int,
    onSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Turgencia de piel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OptionSelectionButton(
                    modifier = Modifier.weight(1f),
                    title = "1 segundo\n(Normal)",
                    isSelected = selectedTime == 1,
                    onClick = { onSelect(1) }
                )
                OptionSelectionButton(
                    modifier = Modifier.weight(1f),
                    title = "< 3 segundos\n(Anormal)",
                    isSelected = selectedTime == 3,
                    onClick = { onSelect(3) }
                )
            }
        }
    }
}

@Composable
private fun OptionSelectionButton(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceContainerLow,
        animationSpec = tween(200),
        label = "bgAnim"
    )
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = animatedBg,
        modifier = modifier
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Text field numérico que bloquea caracteres inválidos en tiempo real
 * y auto-selecciona el texto completo al obtener foco.
 */
@Composable
private fun SelectableNumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    allowDecimals: Boolean = false,
    maxDecimals: Int = 2,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onAction: () -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    var isFocused by remember { mutableStateOf(false) }

    // Sincronizar el estado interno si el valor externo cambia (ej. carga inicial)
    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                // Sanitizar comas a puntos si se permiten decimales
                val sanitizedText = if (allowDecimals) {
                    newValue.text.replace(',', '.')
                } else {
                    newValue.text
                }

                // Validar mediante Regex estricto
                val isValid = if (allowDecimals) {
                    sanitizedText.isEmpty() || sanitizedText.matches(Regex("^\\d*(\\.\\d{0,$maxDecimals})?$"))
                } else {
                    sanitizedText.isEmpty() || sanitizedText.matches(Regex("^\\d*$"))
                }

                // Solo actualizar la UI y notificar si cumple la regla
                if (isValid) {
                    val selectionDiff = sanitizedText.length - newValue.text.length
                    val newSelection = TextRange(
                        (newValue.selection.start + selectionDiff).coerceIn(0, sanitizedText.length),
                        (newValue.selection.end + selectionDiff).coerceIn(0, sanitizedText.length)
                    )
                    textFieldValue = TextFieldValue(text = sanitizedText, selection = newSelection)
                    onValueChange(sanitizedText)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && !isFocused) {
                        textFieldValue = textFieldValue.copy(
                            selection = TextRange(0, textFieldValue.text.length)
                        )
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onAction() },
                onDone = { onAction() }
            ),
            singleLine = true
        )
    }
}

@Composable
fun SavePhysiologicalConstsDialog(
    modifier: Modifier = Modifier,
    patientName: String,
    recordDate: String,
    isEditMode: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .heightIn(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isEditMode) "Actualizar Constantes" else "Guardar Constantes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isEditMode) {
                        "¿Deseas actualizar la información de las constantes fisiológicas con los nuevos cambios?"
                    } else {
                        "¿Deseas guardar las constantes fisiológicas? Podrás consultarlas o modificarlas más adelante."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DataRow(label = "Paciente", value = patientName)
                        DataRow(label = "Fecha de registro", value = recordDate)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DataRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// -------------------------------------------------------------
// Evaluadores de Rangos Clínicos
// -------------------------------------------------------------

private fun evaluateTemperature(value: String, speciesId: Int): RangeEvaluation {
    val temp = value.toDoubleOrNull()
    return when (speciesId) {
        Constants.CANINE_SPECIES_CATALOG -> {
            val normalDesc = "Rango normal: 37.6 - 39.2 °C"
            if (temp == null) return RangeEvaluation("", normalDesc, RangeStatus.DEFAULT)
            when {
                temp in 34.5..37.5 -> RangeEvaluation("Hipotermia", normalDesc, RangeStatus.LOW)
                temp in 37.6..39.2 -> RangeEvaluation("Normal", normalDesc, RangeStatus.NORMAL)
                temp in 39.3..39.5 -> RangeEvaluation("Hipertermia", normalDesc, RangeStatus.HIGH)
                temp in 39.6..41.0 -> RangeEvaluation("Fiebre", normalDesc, RangeStatus.FEVER)
                temp < 34.5 -> RangeEvaluation("Hipotermia severa", normalDesc, RangeStatus.LOW)
                else -> RangeEvaluation("Fiebre severa", normalDesc, RangeStatus.FEVER)
            }
        }
        Constants.FELINE_SPECIES_CATALOG -> {
            val normalDesc = "Rango normal: 38.2 - 39.2 °C"
            if (temp == null) return RangeEvaluation("", normalDesc, RangeStatus.DEFAULT)
            when {
                temp in 34.5..38.1 -> RangeEvaluation("Hipotermia", normalDesc, RangeStatus.LOW)
                temp in 38.2..39.2 -> RangeEvaluation("Normal", normalDesc, RangeStatus.NORMAL)
                temp in 39.3..39.4 -> RangeEvaluation("Hipertermia", normalDesc, RangeStatus.HIGH)
                temp in 39.5..41.0 -> RangeEvaluation("Fiebre", normalDesc, RangeStatus.FEVER)
                temp < 34.5 -> RangeEvaluation("Hipotermia severa", normalDesc, RangeStatus.LOW)
                else -> RangeEvaluation("Fiebre severa", normalDesc, RangeStatus.FEVER)
            }
        }
        else -> RangeEvaluation("", "", RangeStatus.DEFAULT)
    }
}

private fun evaluateHeartRate(value: String, speciesId: Int): RangeEvaluation {
    val rate = value.toIntOrNull()
    return when (speciesId) {
        Constants.CANINE_SPECIES_CATALOG -> {
            val normalDesc = "Rango normal: 60 - 180 LPM"
            if (rate == null) return RangeEvaluation("", normalDesc, RangeStatus.DEFAULT)
            when {
                rate in 24..59 -> RangeEvaluation("Baja", normalDesc, RangeStatus.LOW)
                rate in 60..180 -> RangeEvaluation("Normal", normalDesc, RangeStatus.NORMAL)
                rate in 181..220 -> RangeEvaluation("Alta", normalDesc, RangeStatus.HIGH)
                rate < 24 -> RangeEvaluation("Baja crítica", normalDesc, RangeStatus.LOW)
                else -> RangeEvaluation("Alta crítica", normalDesc, RangeStatus.FEVER)
            }
        }
        Constants.FELINE_SPECIES_CATALOG -> {
            val normalDesc = "Rango normal: 140 - 220 LPM"
            if (rate == null) return RangeEvaluation("", normalDesc, RangeStatus.DEFAULT)
            when {
                rate in 60..139 -> RangeEvaluation("Baja", normalDesc, RangeStatus.LOW)
                rate in 140..220 -> RangeEvaluation("Normal", normalDesc, RangeStatus.NORMAL)
                rate in 221..250 -> RangeEvaluation("Alta", normalDesc, RangeStatus.HIGH)
                rate < 60 -> RangeEvaluation("Baja crítica", normalDesc, RangeStatus.LOW)
                else -> RangeEvaluation("Alta crítica", normalDesc, RangeStatus.FEVER)
            }
        }
        else -> RangeEvaluation("", "", RangeStatus.DEFAULT)
    }
}

private fun evaluateRespiratoryRate(value: String, speciesId: Int): RangeEvaluation {
    val rate = value.toIntOrNull()
    return when (speciesId) {
        Constants.CANINE_SPECIES_CATALOG -> {
            val normalDesc = "Rango normal: 10 - 30 RPM"
            if (rate == null) return RangeEvaluation("", normalDesc, RangeStatus.DEFAULT)
            when {
                rate in 5..9 -> RangeEvaluation("Baja", normalDesc, RangeStatus.LOW)
                rate in 10..30 -> RangeEvaluation("Normal", normalDesc, RangeStatus.NORMAL)
                rate in 31..60 -> RangeEvaluation("Alta", normalDesc, RangeStatus.HIGH)
                rate < 5 -> RangeEvaluation("Baja crítica", normalDesc, RangeStatus.LOW)
                else -> RangeEvaluation("Alta crítica", normalDesc, RangeStatus.FEVER)
            }
        }
        Constants.FELINE_SPECIES_CATALOG -> {
            val normalDesc = "Rango normal: 24 - 42 RPM"
            if (rate == null) return RangeEvaluation("", normalDesc, RangeStatus.DEFAULT)
            when {
                rate in 15..23 -> RangeEvaluation("Baja", normalDesc, RangeStatus.LOW)
                rate in 24..42 -> RangeEvaluation("Normal", normalDesc, RangeStatus.NORMAL)
                rate in 43..60 -> RangeEvaluation("Alta", normalDesc, RangeStatus.HIGH)
                rate < 15 -> RangeEvaluation("Baja crítica", normalDesc, RangeStatus.LOW)
                else -> RangeEvaluation("Alta crítica", normalDesc, RangeStatus.FEVER)
            }
        }
        else -> RangeEvaluation("", "", RangeStatus.DEFAULT)
    }
}

@Composable
private fun getRangeColors(status: RangeStatus): Pair<Color, Color> {
    val custom = MaterialTheme.customColors
    return when (status) {
        RangeStatus.LOW -> custom.rangeHypoContainer to custom.onRangeHypoContainer
        RangeStatus.NORMAL -> custom.rangeNormalContainer to custom.onRangeNormalContainer
        RangeStatus.HIGH -> custom.rangeHyperContainer to custom.onRangeHyperContainer
        RangeStatus.FEVER -> custom.rangeFeverContainer to custom.onRangeFeverContainer
        RangeStatus.DEFAULT -> MaterialTheme.colorScheme.surfaceContainerHighest to MaterialTheme.colorScheme.onSurfaceVariant
    }
}