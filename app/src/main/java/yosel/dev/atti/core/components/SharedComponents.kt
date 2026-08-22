package yosel.dev.atti.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.getIconSpecies
import yosel.dev.atti.ui.theme.AttiTheme
import yosel.dev.atti.ui.theme.customColors
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AttiSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onFilterClick: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (onFilterClick != null) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Filtros",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
fun SnackBarError(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(12.dp),
        action = {
            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar notificación",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SnackBarSuccess(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(12.dp),
        action = {
            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar notificación",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle, // O el icono de éxito que prefieras
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TopBarGlobal(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        },
        modifier = modifier,
        navigationIcon = {

            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

enum class SnackbarType {
    SUCCESS,
    ERROR
}

class CustomSnackbarVisuals(
    override val message: String,
    val type: SnackbarType = SnackbarType.ERROR,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = true
) : SnackbarVisuals

suspend fun SnackbarHostState.showCustomSnackbar(
    message: String,
    type: SnackbarType
) {
    showSnackbar(
        CustomSnackbarVisuals(
            message = message,
            type = type
        )
    )
}

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        val customVisuals = data.visuals as? CustomSnackbarVisuals
        when (customVisuals?.type) {
            SnackbarType.SUCCESS -> SnackBarSuccess(data = data)
            SnackbarType.ERROR, null -> SnackBarError(data = data)
        }
    }
}

@Composable
fun InputFieldGlobal(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
            )
        },
        isError = isError,
        supportingText = if (isError && !errorMessage.isNullOrEmpty()) {
            { Text(text = errorMessage) }
        } else null,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            errorPlaceholderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
    )
}

@Composable
fun InputFieldWithTextGlobal(
    modifier: Modifier = Modifier,
    label: String,
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    // Captura de lambdas para garantizar la versión más actualizada dentro de las corrutinas
    val currentOnClick by rememberUpdatedState(onClick)

    val interactionSource = remember { MutableInteractionSource() }

    // 1. Escuchamos el estado de enfoque del TextField
    val isFocused by interactionSource.collectIsFocusedAsState()

    // 2. Determinamos el color del label según la prioridad de estado: Error > Enfocado > Defecto
    val labelColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    // Modificador condicional para manejar clics cuando readOnly = true y existe onClick
    val clickableModifier = if (readOnly && currentOnClick != null) {
        val onClick = currentOnClick
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            onClick?.invoke()
        }
    } else {
        Modifier
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Label externo superior más destacado y configurable
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = labelColor
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null
                )
            },
            isError = isError,
            supportingText = if (isError && !errorMessage.isNullOrEmpty()) {
                { Text(text = errorMessage) }
            } else null,
            readOnly = readOnly,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                // Remueve las líneas indicadoras inferiores por defecto de Material 3
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                // Colores para el leadingIcon según el estado
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                errorLeadingIconColor = MaterialTheme.colorScheme.error,
                errorPlaceholderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(clickableModifier),
            placeholder = {
                Text(
                    text = placeHolder,
                )
            }
        )
    }
}

@Composable
fun EmptyGlobal(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    icon: ImageVector = Icons.Outlined.Inbox,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    showAction: Boolean = false,
    onClickAction: () -> Unit = {},
    iconButton: ImageVector = Icons.Outlined.Replay,
    textButton: String = "Reintentar"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (showAction) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onClickAction
            ) {
                Icon(
                    imageVector = iconButton,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = textButton)
            }
        }
    }
}

@Composable
fun NoSearchResultsState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier,
    nameResult: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No encontramos $nameResult que coincidan con \"$query\". Prueba con otro nombre o limpia la búsqueda.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        FilledTonalButton(
            onClick = onClearSearch,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Limpiar búsqueda")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatalogBottomSheet(
    catalogName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var nameValue by remember { mutableStateOf("") }
    var isTouched by remember { mutableStateOf(false) }

    val isError = isTouched && nameValue.isBlank()

    LaunchedEffect(Unit) {
        delay(300.milliseconds)
        focusRequester.requestFocus()
    }

    // Cierre animado para interacciones manuales (Botón 'X' o toque fuera)
    fun dismissWithAnimation() {
        coroutineScope.launch {
            focusManager.clearFocus()
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            // Header con Título y Botón 'X'
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Agregar $catalogName",
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

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de Texto
            InputFieldWithTextGlobal(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = "Nombre $catalogName",
                placeHolder = "ej. $catalogName",
                value = nameValue,
                onValueChange = {
                    nameValue = it
                    if (!isTouched && it.isNotBlank()) {
                        isTouched = true
                    }
                },
                leadingIcon = Icons.Outlined.Label,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                isError = isError,
                errorMessage = "Este campo no puede estar vacío"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Guardar
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // 1. Ocultar el teclado inmediatamente
                    focusManager.clearFocus()
                    // 2. Disparar el guardado sin cerrar la hoja localmente
                    onSave(nameValue.trim())
                },
                enabled = nameValue.isNotBlank(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = "Guardar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guardar $catalogName",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private data class StatusChipConfig(
    val backgroundColor: Color,
    val textColor: Color,
    val circleColor: Color,
    val iconColor: Color,
    val label: String,
    val icon: ImageVector
)

@Composable
fun StatusChip(
    status: Int,
    modifier: Modifier = Modifier
) {
    val customColors = MaterialTheme.customColors

    // Mapeo de colores, texto e ícono según el estado
    val (backgroundColor, textColor, containerIconColor, iconColor, label, icon) = when (status) {
        Constants.ACTIVE_PATIENT_STATUS -> StatusChipConfig(
            backgroundColor = customColors.active,
            textColor = customColors.onActive,
            circleColor = customColors.activeContainer,
            iconColor = customColors.onActiveContainer,
            label = "Activo",
            icon = Icons.Default.Check
        )
        Constants.INACTIVE_PATIENT_STATUS -> StatusChipConfig(
            backgroundColor = customColors.inactive,
            textColor = customColors.onInactive,
            circleColor = customColors.inactiveContainer,
            iconColor = customColors.onInactiveContainer,
            label = "Inactivo",
            icon = Icons.Default.Close
        )
        Constants.DELETED_PATIENT_STATUS -> StatusChipConfig(
            backgroundColor = customColors.deleted,
            textColor = customColors.onDeleted,
            circleColor = customColors.deletedContainer,
            iconColor = customColors.onDeletedContainer,
            label = "Eliminado",
            icon = Icons.Default.Delete
        )
        else -> StatusChipConfig(
            backgroundColor = customColors.inactive,
            textColor = customColors.onInactive,
            circleColor = customColors.inactiveContainer,
            iconColor = customColors.onInactiveContainer,
            label = "Desconocido",
            icon = Icons.Default.Close
        )
    }

    Surface(
        modifier = modifier,
        shape = CircleShape, // Forma de cápsula (Pill Shape)
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = containerIconColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun StatusChipShort(
    status: Int,
    modifier: Modifier = Modifier
) {
    val customColors = MaterialTheme.customColors

    // Mapeo de colores, texto e ícono según el estado
    val (backgroundColor, textColor, containerIconColor, iconColor, label, icon) = when (status) {
        Constants.ACTIVE_PATIENT_STATUS -> StatusChipConfig(
            backgroundColor = customColors.active,
            textColor = customColors.active,
            circleColor = customColors.active,
            iconColor = customColors.onActive,
            label = "Activo",
            icon = Icons.Default.Check
        )
        Constants.INACTIVE_PATIENT_STATUS -> StatusChipConfig(
            backgroundColor = customColors.inactive,
            textColor = customColors.inactive,
            circleColor = customColors.inactive,
            iconColor = customColors.onInactive,
            label = "Inactivo",
            icon = Icons.Default.Close
        )
        Constants.DELETED_PATIENT_STATUS -> StatusChipConfig(
            backgroundColor = customColors.deleted,
            textColor = customColors.deleted,
            circleColor = customColors.deleted,
            iconColor = customColors.onDeleted,
            label = "Eliminado",
            icon = Icons.Default.Delete
        )
        else -> StatusChipConfig(
            backgroundColor = customColors.inactive,
            textColor = customColors.inactive,
            circleColor = customColors.inactive,
            iconColor = customColors.onInactive,
            label = "Desconocido",
            icon = Icons.Default.Close
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color = containerIconColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
        }

        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AppCatalogSelector(
    selectedCatalog: AppCatalogModel?,
    onOpenSheet: () -> Unit,
    icon: ImageVector,
    emptyText: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpenSheet
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
                    color = if (selectedCatalog != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selectedCatalog != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = selectedCatalog?.name ?: emptyText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedCatalog != null) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedCatalog != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onOpenSheet
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (selectedCatalog == null) Icons.Filled.Link else Icons.Filled.LinkOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    icon: ImageVector,
    showIcon: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showIcon){
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp) // Un poco más grande que el diseño
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAppCatalogBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    search: String,
    onSearchChange:(String) -> Unit,
    filteredAppCatalogs: List<AppCatalogModel>,
    selectedAppCatalog: AppCatalogModel?,
    onSelectAppCatalog:(AppCatalogModel) -> Unit,
    showAddAppCatalogDialog:() -> Unit,
    catalogosEmpty: Boolean
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
                onDismiss()
                onComplete?.invoke()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
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
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { dismissWithAnimation() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AttiSearchBar(
                    modifier = Modifier.weight(1f),
                    value = search,
                    onValueChange = { onSearchChange(it) },
                    placeholder = "Buscar por nombre"
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = showAddAppCatalogDialog
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Agregar catalog"
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            when{
                catalogosEmpty ->{
                    EmptyGlobal(
                        title = "Aún no hay catálogos",
                        subTitle = "Agrega tu primer catálogo para comenzar con la configuración."
                    )
                }
                filteredAppCatalogs.isEmpty() -> {
                    NoSearchResultsState(
                        query = search,
                        onClearSearch = { onSearchChange("") },
                        nameResult = "catalogos"
                    )
                }
                else ->{
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .nestedScroll(rememberNestedScrollInteropConnection()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredAppCatalogs, key = { it.id }) { appCatalog ->
                            val isSelected = appCatalog.id == selectedAppCatalog?.id

                            AppCatalogSelectionCard(
                                modifier = Modifier.animateItem(),
                                appCatalog = appCatalog,
                                isSelected = isSelected,
                                onClick = {
                                    dismissWithAnimation {
                                        onSelectAppCatalog(appCatalog)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppCatalogSelectionCard(
    modifier: Modifier = Modifier,
    appCatalog: AppCatalogModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
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
                        imageVector = Icons.Outlined.Category,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appCatalog.name.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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

@Composable
fun CountBadge(
    count: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun PhoneInputFieldWithTextGlobal(
    modifier: Modifier = Modifier,
    label: String = "Teléfono",
    placeHolder: String = "ej. +502 87654321",
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector = Icons.Outlined.Phone,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    InputFieldWithTextGlobal(
        modifier = modifier,
        label = label,
        placeHolder = placeHolder,
        value = value,
        onValueChange = { input ->
            val isPhoneCharPattern = input.isEmpty() || input.matches(Regex("""^\+?[0-9\s]*$"""))
            if (isPhoneCharPattern) {
                onValueChange(input)
            }
        },
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        ),
        isError = isError,
        errorMessage = errorMessage
    )
}

@Composable
fun PatientConsultationHeaderCard(
    consultation: ConsultationWithDetailsModel,
    modifier: Modifier = Modifier,
    statusContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    statusContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PatientAvatar(
                speciesId = consultation.patient.speciesId,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = consultation.patient.name.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = consultation.patient.breed.ifBlank { "Raza no especificada" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            ConsultationStatusBadge(
                modifier = Modifier.align(Alignment.Top),
                status = "Canino",
                containerColor = statusContainerColor,
                contentColor = statusContentColor
            )
        }
    }
}

@Composable
private fun PatientAvatar(
    speciesId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = getIconSpecies(speciesId)),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConsultationStatusBadge(
    status: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100),
        color = containerColor
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun PatientConsultationHeaderCardPreview() {
    AttiTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)){
            PatientConsultationHeaderCard(
                consultation = ConsultationWithDetailsModel(
                    patient = PatientModel(
                        name = "Max",
                        breed = "Labrador",
                        speciesId = 1
                    )
                )
            )
        }
    }
}