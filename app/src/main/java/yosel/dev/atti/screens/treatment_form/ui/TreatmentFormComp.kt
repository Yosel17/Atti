package yosel.dev.atti.screens.treatment_form.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.PatientConsultationHeaderHero
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.utils.formatPrice
import yosel.dev.atti.core.utils.getFormattedCurrentDate
import yosel.dev.atti.screens.service_form.ui.dashedBorder
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BodyTreatmentForm(
    modifier: Modifier = Modifier,
    state: TreatmentFormState,
    onAction: (TreatmentFormAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isButtonEnabled = if (state.isEditMode) {
        state.formInputState.isValid && state.formInputState.hasChangesFrom(state.initialFormInputState)
    } else {
        state.formInputState.isValid
    }

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
            Spacer(modifier = Modifier.height(18.dp))

            // Tabs Selector
            TreatmentTabs(
                selectedTab = state.currentTab,
                productCount = state.formInputState.selectedProducts.size,
                serviceCount = state.formInputState.selectedServices.size,
                onTabSelected = { onAction(TreatmentFormAction.OnTabSelected(it)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (state.currentTab) {
                TreatmentTab.PRODUCTS -> {
                    if (state.formInputState.selectedProducts.isEmpty()) {
                        EmptyTreatmentItemsPlaceholder(
                            title = "Sin productos seleccionados",
                            subtitle = "Agrega medicamentos o insumos que se consuman durante esta consulta.",
                            icon = Icons.Outlined.Inventory2,
                            buttonText = "Vincular producto",
                            onClick = { onAction(TreatmentFormAction.OnOpenProductSheet) }
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            state.formInputState.selectedProducts.forEach { item ->
                                key(item.productWithDetails.product.id) {
                                    RemovableProductItem(
                                        item = item,
                                        onIncrement = { onAction(TreatmentFormAction.OnIncrementProduct(item.productWithDetails.product.id)) },
                                        onDecrement = { onAction(TreatmentFormAction.OnDecrementProduct(item.productWithDetails.product.id)) },
                                        onRemove = { onAction(TreatmentFormAction.OnRemoveProduct(item.productWithDetails.product.id)) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            AddMoreItemsDashedButton(
                                text = "Añadir más productos",
                                onClick = { onAction(TreatmentFormAction.OnOpenProductSheet) }
                            )
                        }
                    }
                }
                TreatmentTab.SERVICES -> {
                    if (state.formInputState.selectedServices.isEmpty()) {
                        EmptyTreatmentItemsPlaceholder(
                            title = "Sin servicios seleccionados",
                            subtitle = "Agrega los servicios clínicos o quirúrgicos realizados al paciente.",
                            icon = Icons.Outlined.MedicalServices,
                            buttonText = "Vincular servicio",
                            onClick = { onAction(TreatmentFormAction.OnOpenServiceSheet) }
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            state.formInputState.selectedServices.forEach { item ->
                                key(item.serviceWithDetails.service.id) {
                                    RemovableServiceItem(
                                        item = item,
                                        onIncrement = { onAction(TreatmentFormAction.OnIncrementService(item.serviceWithDetails.service.id)) },
                                        onDecrement = { onAction(TreatmentFormAction.OnDecrementService(item.serviceWithDetails.service.id)) },
                                        onRemove = { onAction(TreatmentFormAction.OnRemoveService(item.serviceWithDetails.service.id)) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            AddMoreItemsDashedButton(
                                text = "Añadir más servicios",
                                onClick = { onAction(TreatmentFormAction.OnOpenServiceSheet) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Resumen inferior
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOTAL A PAGAR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Q ${state.formInputState.totalAmount.formatPrice()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(TreatmentFormAction.ToggleSaveDialog(show = true))
            },
            enabled = isButtonEnabled,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Outlined.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isEditMode) "Guardar edición" else "Guardar Tratamiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TreatmentTabs(
    selectedTab: TreatmentTab,
    productCount: Int,
    serviceCount: Int,
    onTabSelected: (TreatmentTab) -> Unit
) {
    val selectedIndex = if (selectedTab == TreatmentTab.PRODUCTS) 0 else 1

    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Tab(
            selected = selectedTab == TreatmentTab.PRODUCTS,
            onClick = { onTabSelected(TreatmentTab.PRODUCTS) },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (productCount > 0) "Productos ($productCount)" else "Productos",
                        fontWeight = if (selectedTab == TreatmentTab.PRODUCTS) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        )
        Tab(
            selected = selectedTab == TreatmentTab.SERVICES,
            onClick = { onTabSelected(TreatmentTab.SERVICES) },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (serviceCount > 0) "Servicios ($serviceCount)" else "Servicios",
                        fontWeight = if (selectedTab == TreatmentTab.SERVICES) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        )
    }
}

@Composable
private fun RemovableProductItem(
    item: SelectedTreatmentProduct,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    var isVisible by remember(item.productWithDetails.product.id) { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(200)),
        exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
    ) {
        TreatmentSelectedCard(
            title = item.productWithDetails.product.commercialName,
            subtitle = item.productWithDetails.unitType.name,
            price = item.productWithDetails.product.salePrice,
            quantity = item.quantity,
            canIncrement = item.quantity < item.productWithDetails.product.stock,
            canDecrement = item.quantity > 1,
            icon = Icons.Outlined.Inventory2,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
            onRemove = { isVisible = false }
        )
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(250.milliseconds)
            onRemove()
        }
    }
}

@Composable
private fun RemovableServiceItem(
    item: SelectedTreatmentService,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    var isVisible by remember(item.serviceWithDetails.service.id) { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(200)),
        exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
    ) {
        TreatmentSelectedCard(
            title = item.serviceWithDetails.service.name,
            subtitle = item.serviceWithDetails.category.name,
            price = item.serviceWithDetails.service.salePrice,
            quantity = item.quantity,
            canIncrement = true,
            canDecrement = item.quantity > 1,
            icon = Icons.Filled.MedicalServices,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
            onRemove = { isVisible = false }
        )
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(250.milliseconds)
            onRemove()
        }
    }
}

@Composable
private fun TreatmentSelectedCard(
    title: String,
    subtitle: String,
    price: Double,
    quantity: Int,
    canIncrement: Boolean,
    canDecrement: Boolean,
    icon: ImageVector,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Q ${price.formatPrice()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Contador estilo Pill
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        enabled = canDecrement,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Disminuir",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        enabled = canIncrement,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Aumentar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun AddMoreItemsDashedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .dashedBorder(
                width = 1.5.dp,
                color = outlineColor,
                cornerRadius = 18.dp,
                dashLength = 8.dp,
                gapLength = 6.dp
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptyTreatmentItemsPlaceholder(
    title: String,
    subtitle: String,
    icon: ImageVector,
    buttonText: String,
    onClick: () -> Unit,
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
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onClick,
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
                Text(text = buttonText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// --- BottomSheet de Productos ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectProductBottomSheet(
    onDismiss: () -> Unit,
    search: String,
    onSearchChange: (String) -> Unit,
    filteredProducts: List<ProductWithDetailsModel>,
    tempSelectedProductIds: Set<String>,
    onToggleSelectProduct: (ProductWithDetailsModel) -> Unit,
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
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Cerrar")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AttiSearchBar(
                value = search,
                onValueChange = onSearchChange,
                placeholder = "Buscar producto por nombre o marca"
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                productsEmpty -> {
                    EmptyGlobal(
                        title = "Aún no hay productos",
                        subTitle = "No hay productos registrados en el inventario."
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
                        items(
                            items = filteredProducts,
                            key = { it.product.id }
                        ) { productWithDetails ->
                            val isSelected = tempSelectedProductIds.contains(productWithDetails.product.id)
                            val hasStock = productWithDetails.product.stock > 0

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .clickable(enabled = hasStock) {
                                        onToggleSelectProduct(productWithDetails)
                                    },
                                shape = RoundedCornerShape(18.dp),
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
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { if (hasStock) onToggleSelectProduct(productWithDetails) },
                                        enabled = hasStock,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = productWithDetails.product.commercialName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (hasStock) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = productWithDetails.unitType.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Q ${productWithDetails.product.salePrice.formatPrice()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier.align(Alignment.Top),
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (hasStock) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = if (hasStock) "Stock: ${productWithDetails.product.stock}" else "Sin stock",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (hasStock) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { dismissWithAnimation { onConfirmSelection() } },
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
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

// --- BottomSheet de Servicios ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectServiceBottomSheet(
    onDismiss: () -> Unit,
    search: String,
    onSearchChange: (String) -> Unit,
    filteredServices: List<ServiceWithDetailsModel>,
    tempSelectedServiceIds: Set<String>,
    onToggleSelectService: (ServiceWithDetailsModel) -> Unit,
    onConfirmSelection: () -> Unit,
    servicesEmpty: Boolean
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
                    text = "Selecciona los servicios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { dismissWithAnimation() }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Cerrar")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AttiSearchBar(
                value = search,
                onValueChange = onSearchChange,
                placeholder = "Buscar servicio por nombre..."
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                servicesEmpty -> {
                    EmptyGlobal(
                        title = "Aún no hay servicios",
                        subTitle = "No hay servicios activos registrados en el sistema."
                    )
                }
                filteredServices.isEmpty() -> {
                    NoSearchResultsState(
                        query = search,
                        onClearSearch = { onSearchChange("") },
                        nameResult = "servicios"
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
                        items(
                            items = filteredServices,
                            key = { it.service.id }
                        ) { serviceWithDetails ->
                            val isSelected = tempSelectedServiceIds.contains(serviceWithDetails.service.id)
                            val hasUnavailableSupply = serviceWithDetails.supplies.any { it.product.product.stock <= 0 }
                            val isAvailable = !hasUnavailableSupply

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .clickable(enabled = isAvailable) {
                                        onToggleSelectService(serviceWithDetails)
                                    },
                                shape = RoundedCornerShape(18.dp),
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
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { if (isAvailable) onToggleSelectService(serviceWithDetails) },
                                        enabled = isAvailable,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = serviceWithDetails.service.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = serviceWithDetails.category.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Q ${serviceWithDetails.service.salePrice.formatPrice()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    if (!isAvailable) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            modifier = Modifier.align(Alignment.Top),
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.errorContainer
                                        ) {
                                            Text(
                                                text = "Sin insumos",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { dismissWithAnimation { onConfirmSelection() } },
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (tempSelectedServiceIds.isNotEmpty())
                                    "Continuar (${tempSelectedServiceIds.size})"
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

// --- Diálogo de Confirmación ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveTreatmentDialog(
    modifier: Modifier = Modifier,
    patientName: String,
    recordDate: String,
    productsCount: Int,
    servicesCount: Int,
    totalPrice: Double,
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
                        imageVector = Icons.Rounded.LocalHospital,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isEditMode) "Actualizar Tratamiento" else "Guardar Tratamiento",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isEditMode) {
                        "¿Deseas actualizar los productos y servicios asociados a esta consulta médica?"
                    } else {
                        "¿Deseas registrar los productos y servicios seleccionados en el expediente del paciente?"
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
                        DataRow(label = "Fecha", value = recordDate)
                        DataRow(label = "Productos añadidos", value = "$productsCount")
                        DataRow(label = "Servicios añadidos", value = "$servicesCount")
                        DataRow(label = "Total estimado", value = "Q ${totalPrice.formatPrice()}")
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
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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