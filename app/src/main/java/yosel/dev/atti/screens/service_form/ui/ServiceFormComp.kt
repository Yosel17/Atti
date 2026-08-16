package yosel.dev.atti.screens.service_form.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import yosel.dev.atti.core.components.AppCatalogSelector
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.utils.Constants
import kotlin.math.roundToInt

@Composable
fun BodyServiceForm(
    modifier: Modifier = Modifier,
    state: ServiceFormState,
    onAction: (ServiceFormAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isButtonEnabled = if (state.isEditMode) {
        state.formInputState.isValid && state.formInputState.hasChangesFrom(state.initialFormInputState)
    } else {
        state.formInputState.isValid
    }

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            GeneralInformationSection(
                formInputState = state.formInputState,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(28.dp))
            PricesAndCostsSection(
                formInputState = state.formInputState,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(ServiceFormAction.OnSaveService)
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
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isEditMode) "Guardar edición" else "Guardar Servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GeneralInformationSection(
    modifier: Modifier = Modifier,
    formInputState: ServiceFormInputsState,
    onAction: (ServiceFormAction) -> Unit
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
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Información General",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información General",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InputFieldGlobal(
                label = "Nombre del Servicio",
                placeholder = "Ej. Consulta General Canina",
                value = formInputState.name,
                onValueChange = {
                    onAction(
                        ServiceFormAction.OnChangeValueFormInputState(
                            value = it,
                            field = Constants.SERVICE_NAME_FIELD
                        )
                    )
                },
                leadingIcon = Icons.Filled.MedicalServices,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = formInputState.isError(Constants.SERVICE_NAME_FIELD),
                errorMessage = "El nombre del servicio es obligatorio"
            )
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle(
                title = "Categoría",
                icon = Icons.Filled.Category,
                showIcon = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppCatalogSelector(
                selectedCatalog = formInputState.selectedCategory,
                onOpenSheet = {
                    onAction(ServiceFormAction.OnOpenCategorySheet)
                },
                icon = Icons.Filled.Category,
                emptyText = "Selecciona una categoría"
            )
        }
    }
}

@Composable
private fun PricesAndCostsSection(
    modifier: Modifier = Modifier,
    formInputState: ServiceFormInputsState,
    onAction: (ServiceFormAction) -> Unit
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
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Payments,
                    contentDescription = "Precios y Costos",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Precios y Costos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            InputFieldGlobal(
                label = "Precio de venta",
                placeholder = "0.00",
                value = formInputState.salePrice,
                onValueChange = { input ->
                    val sanitizedInput = input.replace(',', '.')
                    if (sanitizedInput.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                        onAction(
                            ServiceFormAction.OnChangeValueFormInputState(
                                value = sanitizedInput,
                                field = Constants.SERVICE_SALE_PRICE_FIELD
                            )
                        )
                    }
                },
                leadingIcon = Icons.Filled.PointOfSale,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = if (formInputState.expenseMode == ExpenseMode.MANUAL) ImeAction.Next else ImeAction.Done
                ),
                isError = formInputState.isError(Constants.SERVICE_SALE_PRICE_FIELD),
                errorMessage = "El precio de venta es obligatorio"
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Gastos de insumos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            ExpenseModeSelector(
                selectedMode = formInputState.expenseMode,
                onModeSelected = { onAction(ServiceFormAction.OnChangeExpenseMode(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = formInputState.expenseMode,
                transitionSpec = {
                    val duration = 220
                    // Determina la dirección del slide según la transición
                    if (targetState == ExpenseMode.LINK_PRODUCTS) {
                        // De MANUAL a LINK_PRODUCTS: entra por la derecha, sale hacia la izquierda
                        (slideInHorizontally(animationSpec = tween(duration)) { width -> width / 4 } + fadeIn(animationSpec = tween(duration)))
                            .togetherWith(
                                slideOutHorizontally(animationSpec = tween(duration)) { width -> -width / 4 } + fadeOut(animationSpec = tween(duration))
                            )
                    } else {
                        // De LINK_PRODUCTS a MANUAL: entra por la izquierda, sale hacia la derecha
                        (slideInHorizontally(animationSpec = tween(duration)) { width -> -width / 4 } + fadeIn(animationSpec = tween(duration)))
                            .togetherWith(
                                slideOutHorizontally(animationSpec = tween(duration)) { width -> width / 4 } + fadeOut(animationSpec = tween(duration))
                            )
                    }.using(
                        // Adapta suavemente la altura del contenedor si un contenido es más alto que el otro
                        SizeTransform(clip = false)
                    )
                },
                label = "ExpenseModeContentAnimation"
            ) { targetMode ->
                when (targetMode) {
                    ExpenseMode.MANUAL -> {
                        InputFieldGlobal(
                            label = "Costo total estimado",
                            placeholder = "0.00",
                            value = formInputState.estimatedCost,
                            onValueChange = { input ->
                                val sanitizedInput = input.replace(',', '.')
                                if (sanitizedInput.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                                    onAction(
                                        ServiceFormAction.OnChangeValueFormInputState(
                                            value = sanitizedInput,
                                            field = Constants.SERVICE_ESTIMATED_COST_FIELD
                                        )
                                    )
                                }
                            },
                            leadingIcon = Icons.Filled.AttachMoney,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            isError = formInputState.isError(Constants.SERVICE_ESTIMATED_COST_FIELD),
                            errorMessage = "El costo estimado es obligatorio"
                        )
                    }

                    ExpenseMode.LINK_PRODUCTS -> {
                        if (formInputState.selectedProducts.isEmpty()) {
                            EmptySuppliesPlaceholder(
                                onLinkProductClick = { onAction(ServiceFormAction.OnOpenProductSheet) }
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                formInputState.selectedProducts.forEach { item ->
                                    SelectedProductSupplyCard(
                                        item = item,
                                        onIncrement = { onAction(ServiceFormAction.OnIncrementProductQuantity(item.product.id)) },
                                        onDecrement = { onAction(ServiceFormAction.OnDecrementProductQuantity(item.product.id)) },
                                        onRemove = { onAction(ServiceFormAction.OnRemoveProductSupply(item.product.id)) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onAction(ServiceFormAction.OnOpenProductSheet) },
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Modificar insumos vinculados")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseModeSelector(
    selectedMode: ExpenseMode,
    onModeSelected: (ExpenseMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isManual = selectedMode == ExpenseMode.MANUAL

    // Animación de color de fondo para cada píldora
    val manualBgColor by animateColorAsState(
        targetValue = if (isManual) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "ManualBgColor"
    )
    val linkBgColor by animateColorAsState(
        targetValue = if (!isManual) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "LinkBgColor"
    )

    // Animación de color para los textos
    val manualTextColor by animateColorAsState(
        targetValue = if (isManual) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 250),
        label = "ManualTextColor"
    )
    val linkTextColor by animateColorAsState(
        targetValue = if (!isManual) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 250),
        label = "LinkTextColor"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(100.dp))
                    .background(manualBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ) { onModeSelected(ExpenseMode.MANUAL) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingreso Manual",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isManual) FontWeight.Bold else FontWeight.Medium,
                    color = manualTextColor
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(100.dp))
                    .background(linkBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ) { onModeSelected(ExpenseMode.LINK_PRODUCTS) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vincular Productos",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (!isManual) FontWeight.Bold else FontWeight.Medium,
                    color = linkTextColor
                )
            }
        }
    }
}

@Composable
private fun EmptySuppliesPlaceholder(
    onLinkProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .dashedBorder(
                width = 1.5.dp,
                color = outlineColor,
                cornerRadius = 20.dp,
                dashLength = 8.dp,
                gapLength = 6.dp
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Sin productos vinculados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Agrega productos que se consuman\nautomáticamente con este servicio.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onLinkProductClick,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Vincular producto",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SelectedProductSupplyCard(
    item: SelectedProductSupply,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
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
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.commercialName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.product.brand.isNotBlank()) {
                    Text(
                        text = item.product.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(32.dp),
                    enabled = item.quantity > 1.0
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Disminuir",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Aumentar",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectProductBottomSheet(
    onDismiss: () -> Unit,
    search: String,
    onSearchChange: (String) -> Unit,
    filteredProducts: List<ProductModel>,
    tempSelectedProductIds: Set<String>,
    onToggleSelectProduct: (ProductModel) -> Unit,
    onConfirmSelection: () -> Unit,
    productsEmpty: Boolean
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
        onDismissRequest = { onDismiss() },
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
                    text = "Selecciona los productos",
                    style = MaterialTheme.typography.titleLarge,
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
                value = search,
                onValueChange = { onSearchChange(it) },
                placeholder = "Buscar producto por nombre o marca"
            )

            Spacer(modifier = Modifier.height(16.dp))
            when {
                productsEmpty -> {
                    EmptyGlobal(
                        title = "Aún no hay productos",
                        subTitle = "Registra tus primeros productos en el inventario para vincularlos a este servicio."
                    )
                }
                filteredProducts.isEmpty() -> {
                    NoSearchResultsState(
                        query = search,
                        onClearSearch = { onSearchChange("") },
                        nameResult = "productos"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .nestedScroll(rememberNestedScrollInteropConnection()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            val isSelected = tempSelectedProductIds.contains(product.id)
                            ProductMultiSelectionCard(
                                product = product,
                                isSelected = isSelected,
                                onToggle = { onToggleSelectProduct(product) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            dismissWithAnimation {
                                onConfirmSelection()
                            }
                        },
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (tempSelectedProductIds.isNotEmpty())
                                    "Continuar (${tempSelectedProductIds.size})"
                                else "Continuar",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductMultiSelectionCard(
    product: ProductModel,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(18.dp),
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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.commercialName.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (product.brand.isNotBlank()) {
                    Text(
                        text = "Marca: ${product.brand}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (product.stock > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = "Stock: ${product.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

fun Modifier.dashedBorder(
    width: Dp = 1.5.dp,
    color: Color,
    cornerRadius: Dp = 16.dp,
    dashLength: Dp = 8.dp,
    gapLength: Dp = 6.dp
) = this.drawBehind {
    val strokeWidthPx = width.toPx()
    val cornerRadiusPx = cornerRadius.toPx()
    val dashLengthPx = dashLength.toPx()
    val gapLengthPx = gapLength.toPx()

    val stroke = Stroke(
        width = strokeWidthPx,
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLengthPx, gapLengthPx), 0f
        )
    )
    val halfStroke = strokeWidthPx / 2
    val outlinePath = Path().apply {
        addRoundRect(
            RoundRect(
                left = halfStroke,
                top = halfStroke,
                right = size.width - halfStroke,
                bottom = size.height - halfStroke,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx
            )
        )
    }
    drawPath(path = outlinePath, color = color, style = stroke)
}