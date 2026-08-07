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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

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
    onCopyClick: (label: String, value: String) -> Unit = { _, _ -> },
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    showCopyButton: Boolean = false
) {
    val clipboard = LocalClipboard.current
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    if (readOnly && onClick != null) {
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    onClick()
                }
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder)},
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (showCopyButton) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText(label, value)
                            clipboard.setClipEntry(ClipEntry(clipData))
                            onCopyClick(label, value)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copiar $label",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorLeadingIconColor = MaterialTheme.colorScheme.error
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
    onCopyClick: (label: String, value: String) -> Unit = { _, _ -> },
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    showCopyButton: Boolean = false
) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    // Captura de lambdas para garantizar la versión más actualizada dentro de las corrutinas
    val currentOnCopyClick by rememberUpdatedState(onCopyClick)
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
            trailingIcon = {
                if (showCopyButton) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val clipData = ClipData.newPlainText(label, value)
                                clipboard.setClipEntry(ClipEntry(clipData))
                                currentOnCopyClick(label, value)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copiar $label",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
        if (showAction){
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