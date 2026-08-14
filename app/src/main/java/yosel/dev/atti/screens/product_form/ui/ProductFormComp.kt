package yosel.dev.atti.screens.product_form.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import yosel.dev.atti.core.components.AppCatalogSelector
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.add_patient.ui.AddPatientAction

@Composable
fun BodyProductForm(
    modifier: Modifier = Modifier,
    state: ProductFormState,
    onAction: (ProductFormAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            BasicInformationSection(
                formInputState = state.formInputState,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(28.dp))
            PriceAndSupplier(
                formInputState = state.formInputState,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(28.dp))
            Stock(
                formInputState = state.formInputState,
                onAction = onAction
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(ProductFormAction.RegisterProduct)
            },
            enabled = state.formInputState.isValid,
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
                    text = "Registrar producto",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
private fun BasicInformationSection(
    modifier: Modifier = Modifier,
    formInputState: ProductFormInputsState,
    onAction: (ProductFormAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Informacion",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información Básica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputFieldGlobal(
                label = "Nombre comercial",
                placeholder = "Ej: Antibiótico Amoxipet",
                value = formInputState.commercialName,
                onValueChange = {
                    onAction(ProductFormAction.OnChangeValueFormInputState(
                        value = it,
                        field = Constants.PRODUCT_COMMERCIAL_NAME_FIELD)
                    )
                },
                leadingIcon = Icons.Filled.Badge,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = formInputState.isError(Constants.PRODUCT_COMMERCIAL_NAME_FIELD),
                errorMessage = "El nombre comercial es obligatorio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFieldGlobal(
                label = "Marca",
                placeholder = "Ej: BioVet Labs",
                value = formInputState.brand,
                onValueChange = {
                    onAction(ProductFormAction.OnChangeValueFormInputState(
                        value = it,
                        field = Constants.PRODUCT_BRAND_FIELD)
                    )
                },
                leadingIcon = Icons.Filled.Storefront,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                isError = formInputState.isError(Constants.PRODUCT_BRAND_FIELD),
                errorMessage = "El nombre de la marca es obligatorio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(
                title = "Categoria",
                icon = Icons.Filled.Category,
                showIcon = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppCatalogSelector(
                selectedCatalog = formInputState.selectedCategory,
                onOpenSheet = {
                    onAction(ProductFormAction.OnOpenCategorySheet)
                },
                icon = Icons.Filled.Category,
                emptyText = "Selecciona una categoria"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(
                title = "Unidad de medida",
                icon = Icons.Filled.Straighten,
                showIcon = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppCatalogSelector(
                selectedCatalog = formInputState.selectedUnitType,
                onOpenSheet = {
                    onAction(ProductFormAction.OnOpenUnitsMeasurementSheet)
                },
                icon = Icons.Filled.Straighten,
                emptyText = "Selecciona una unidad de medida"
            )
        }
    }
}

@Composable
fun PriceAndSupplier(
    modifier: Modifier = Modifier,
    formInputState: ProductFormInputsState,
    onAction: (ProductFormAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Column(
            modifier = Modifier.padding(20.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Payments,
                    contentDescription = "precio",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Precios y proveedor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputFieldGlobal(
                label = "Precio de compra",
                placeholder = "0.00",
                value = formInputState.purchasePrice,
                onValueChange = { input ->
                    val sanitizedInput = input.replace(',', '.')

                    if (sanitizedInput.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                        onAction(
                            ProductFormAction.OnChangeValueFormInputState(
                                value = sanitizedInput,
                                field = Constants.PRODUCT_PURCHASE_PRICE_FIELD
                            )
                        )
                    }
                },
                leadingIcon = Icons.Filled.ShoppingCart,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                isError = formInputState.isError(Constants.PRODUCT_PURCHASE_PRICE_FIELD),
                errorMessage = "El precio de compra es obligatorio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFieldGlobal(
                label = "Precio de venta",
                placeholder = "0.00",
                value = formInputState.salePrice,
                onValueChange = { input ->
                    val sanitizedInput = input.replace(',', '.')

                    if (sanitizedInput.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                        onAction(ProductFormAction.OnChangeValueFormInputState(
                            value = input,
                            field = Constants.PRODUCT_SALE_PRICE_FIELD)
                        )
                    }

                },
                leadingIcon = Icons.Filled.PointOfSale,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                isError = formInputState.isError(Constants.PRODUCT_SALE_PRICE_FIELD),
                errorMessage = "El precio de venta es obligatorio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(
                title = "Proveedor",
                icon = Icons.Filled.LocalShipping,
                showIcon = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            SupplierSelector(
                selectedSupplier = formInputState.selectedSupplier,
                onOpenSheet = {
                    onAction(ProductFormAction.OnOpenSupplierSheet)
                },
                icon = Icons.Filled.LocalShipping,
            )
        }
    }
}

@Composable
private fun SupplierSelector(
    selectedSupplier: SupplierModel?,
    onOpenSheet: () -> Unit,
    icon: ImageVector,
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
                    color = if (selectedSupplier != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selectedSupplier != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = selectedSupplier?.name ?: "Selecciona un proveedor",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedSupplier != null) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedSupplier != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onOpenSheet
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (selectedSupplier == null) Icons.Filled.Link else Icons.Filled.LinkOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSupplierBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    search: String,
    onSearchChange:(String) -> Unit,
    filteredSuppliers: List<SupplierModel>,
    selectedSupplier: SupplierModel?,
    onSelectSupplier:(SupplierModel) -> Unit,
    suppliersEmpty: Boolean
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

            AttiSearchBar(
                value = search,
                onValueChange = { onSearchChange(it) },
                placeholder = "Buscar por nombre"
            )

            Spacer(modifier = Modifier.height(16.dp))

            when{
                suppliersEmpty ->{
                    EmptyGlobal(
                        title = "Aún no hay proveedores",
                        subTitle = "Agrega tu primer proveedor para comenzar con la configuración."
                    )
                }
                filteredSuppliers.isEmpty() -> {
                    NoSearchResultsState(
                        query = search,
                        onClearSearch = { onSearchChange("") },
                        nameResult = "proveedores"
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
                        items(filteredSuppliers, key = { it.id }) { supplier ->
                            val isSelected = supplier.id == selectedSupplier?.id

                            SupplierSelectionCard(
                                supplier = supplier,
                                isSelected = isSelected,
                                onClick = {
                                    dismissWithAnimation {
                                        onSelectSupplier(supplier)
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
private fun SupplierSelectionCard(
    supplier: SupplierModel,
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
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = supplier.name.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (supplier.phoneNumber.isNotBlank()) {
                    Text(
                        text = "Tel. ${supplier.phoneNumber}",
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

@Composable
private fun Stock(
    modifier: Modifier = Modifier,
    formInputState: ProductFormInputsState,
    onAction: (ProductFormAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Warehouse,
                    contentDescription = "stock",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stock",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InputFieldGlobal(
                    modifier = Modifier.weight(1f),
                    label = "Stock",
                    placeholder = "0",
                    value = formInputState.stock,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            onAction(
                                ProductFormAction.OnChangeValueFormInputState(
                                    value = it, field = Constants.PRODUCT_STOCK_FIELD
                                )
                            )
                        }
                    },
                    leadingIcon = Icons.Outlined.Warehouse,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = formInputState.isError(Constants.PRODUCT_STOCK_FIELD),
                    errorMessage = "Requerido"
                )

                InputFieldGlobal(
                    modifier = Modifier.weight(1f),
                    label = "Stock min.",
                    placeholder = "0",
                    value = formInputState.minStock,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            onAction(
                                ProductFormAction.OnChangeValueFormInputState(
                                    value = it, field = Constants.PRODUCT_MIN_STOCK_FIELD
                                )
                            )
                        }
                    },
                    leadingIcon = Icons.Outlined.NotificationImportant,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    isError = formInputState.isError(Constants.PRODUCT_MIN_STOCK_FIELD),
                    errorMessage = "Requerido"
                )
            }
        }
    }
}