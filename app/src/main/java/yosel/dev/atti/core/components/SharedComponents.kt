package yosel.dev.atti.core.components

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yosel.dev.atti.core.utils.Constants
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
                tint = MaterialTheme.colorScheme.primary,
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Círculo para el ícono (tal como en la imagen)
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

            // Texto del estado
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}