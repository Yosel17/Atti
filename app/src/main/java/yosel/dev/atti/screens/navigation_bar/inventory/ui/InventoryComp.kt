package yosel.dev.atti.screens.navigation_bar.inventory.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.R
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.AttiTheme
import yosel.dev.atti.ui.theme.CustomColors
import yosel.dev.atti.ui.theme.customColors
import java.util.Locale

private data class InventoryTabData(
    val title: String,
    val icon: ImageVector
)

private enum class InventoryUIStatus {
    LOADING,
    CONTENT,
    EMPTY
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyInventory(
    modifier: Modifier = Modifier,
    state: InventoryState,
    onAction: (InventoryAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val tabs = remember {
        listOf(
            InventoryTabData("Productos", Icons.Filled.Medication),
            InventoryTabData("Servicios", Icons.Filled.MedicalServices),
            InventoryTabData("Proveedores", Icons.Filled.LocalShipping)
        )
    }

    val productListState = rememberLazyListState()
    val serviceListState = rememberLazyListState()
    val supplierListState = rememberLazyListState()

    Column(modifier = modifier) {
        SecondaryTabRow(
            selectedTabIndex = state.selectedTabIndex,
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabData ->
                Tab(
                    selected = state.selectedTabIndex == index,
                    onClick = { onAction(InventoryAction.OnTabSelected(index)) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = tabData.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tabData.title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        }

        AnimatedContent(
            targetState = state.selectedTabIndex,
            label = "InventoryTabContentTransition",
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { 30 }
                )).togetherWith(fadeOut(animationSpec = tween(150)))
            },
            modifier = Modifier.fillMaxSize()
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    val productStatus = when {
                        state.isLoadingProducts -> InventoryUIStatus.LOADING
                        state.products.isNotEmpty() -> InventoryUIStatus.CONTENT
                        else -> InventoryUIStatus.EMPTY
                    }
                    AnimatedContent(
                        targetState = productStatus,
                        label = "ProductContentAnimation",
                        modifier = Modifier.fillMaxSize()
                    ) { status ->
                        when (status) {
                            InventoryUIStatus.CONTENT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 24.dp)
                                ) {
                                    AttiSearchBar(
                                        value = state.productSearchQuery,
                                        onValueChange = { onAction(InventoryAction.OnProductSearchQueryChange(it)) },
                                        placeholder = "Buscar productos...",
                                        onFilterClick = {}
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CountBadge(
                                        modifier = Modifier.fillMaxWidth(),
                                        count = state.filteredProducts.size,
                                        title = "Total de productos"
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AnimatedContent(
                                        targetState = state.filteredProducts.isEmpty(),
                                        label = "ProductSearchAnimation"
                                    ) { isSearchEmpty ->
                                        if (isSearchEmpty) {
                                            NoSearchResultsState(
                                                query = state.productSearchQuery,
                                                onClearSearch = {
                                                    onAction(InventoryAction.OnProductSearchQueryChange(""))
                                                },
                                                nameResult = "productos"
                                            )
                                        } else {
                                            ProductList(
                                                modifier = Modifier.fillMaxSize(),
                                                products = state.filteredProducts,
                                                listState = productListState,
                                                onItemClick = { productId ->
                                                    onNavigationMain(
                                                        Screens.DetailProduct(
                                                            productId = productId
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            InventoryUIStatus.LOADING -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator()
                                }
                            }
                            InventoryUIStatus.EMPTY -> {
                                EmptyProductsState(
                                    onAddProductClick = {
                                        onNavigationMain(Screens.ProductForm())
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    val serviceStatus = when {
                        state.isLoadingServices -> InventoryUIStatus.LOADING
                        state.services.isNotEmpty() -> InventoryUIStatus.CONTENT
                        else -> InventoryUIStatus.EMPTY
                    }
                    AnimatedContent(
                        targetState = serviceStatus,
                        label = "ServiceContentAnimation",
                        modifier = Modifier.fillMaxSize()
                    ) { status ->
                        when (status) {
                            InventoryUIStatus.CONTENT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 24.dp)
                                ) {
                                    AttiSearchBar(
                                        value = state.serviceSearchQuery,
                                        onValueChange = { onAction(InventoryAction.OnServiceSearchQueryChange(it)) },
                                        placeholder = "Buscar servicios...",
                                        onFilterClick = {}
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AnimatedContent(
                                        targetState = state.filteredServices.isEmpty(),
                                        label = "ServiceSearchAnimation"
                                    ) { isSearchEmpty ->
                                        if (isSearchEmpty) {
                                            NoSearchResultsState(
                                                query = state.serviceSearchQuery,
                                                onClearSearch = {
                                                    onAction(InventoryAction.OnServiceSearchQueryChange(""))
                                                },
                                                nameResult = "servicios"
                                            )
                                        } else {
                                            ServiceList(
                                                modifier = Modifier.fillMaxSize(),
                                                services = state.filteredServices,
                                                listState = serviceListState
                                            )
                                        }
                                    }
                                }
                            }
                            InventoryUIStatus.LOADING -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator()
                                }
                            }
                            InventoryUIStatus.EMPTY -> {
                                EmptyServicesState(
                                    onAddServiceClick = {}
                                )
                            }
                        }
                    }
                }
                2 -> {
                    val supplierStatus = when {
                        state.isLoadingSuppliers -> InventoryUIStatus.LOADING
                        state.suppliers.isNotEmpty() -> InventoryUIStatus.CONTENT
                        else -> InventoryUIStatus.EMPTY
                    }
                    AnimatedContent(
                        targetState = supplierStatus,
                        label = "SupplierContentAnimation",
                        modifier = Modifier.fillMaxSize()
                    ) { status ->
                        when (status) {
                            InventoryUIStatus.CONTENT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 24.dp)
                                ) {
                                    AttiSearchBar(
                                        value = state.supplierSearchQuery,
                                        onValueChange = { onAction(InventoryAction.OnSupplierSearchQueryChange(it)) },
                                        placeholder = "Buscar proveedores...",
                                        onFilterClick = {}
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CountBadge(
                                        modifier = Modifier.fillMaxWidth(),
                                        count = state.filteredSuppliers.size,
                                        title = "Total de proveedores"
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AnimatedContent(
                                        targetState = state.filteredSuppliers.isEmpty(),
                                        label = "SupplierSearchAnimation"
                                    ) { isSearchEmpty ->
                                        if (isSearchEmpty) {
                                            NoSearchResultsState(
                                                query = state.supplierSearchQuery,
                                                onClearSearch = {
                                                    onAction(InventoryAction.OnSupplierSearchQueryChange(""))
                                                },
                                                nameResult = "proveedores"
                                            )
                                        } else {
                                            SupplierList(
                                                modifier = Modifier.fillMaxSize(),
                                                suppliers = state.filteredSuppliers,
                                                listState = supplierListState,
                                                onAction = onAction,
                                                onItemClick = { supplierId ->
                                                    onNavigationMain(Screens.DetailSupplier(
                                                        supplierId = supplierId
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            InventoryUIStatus.LOADING -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator()
                                }
                            }
                            InventoryUIStatus.EMPTY -> {
                                EmptySuppliersState(
                                    onAddSupplierClick = {
                                        onNavigationMain(Screens.AddSupplier)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<ProductWithDetailsModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(products.size) }
    val firstProductId = products.firstOrNull()?.product?.id
    LaunchedEffect(products.size, firstProductId) {
        if (products.size > previousCount && products.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = products.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(products, key = { it.product.id }) { productWithDetails ->
            ProductCard(
                productDetails = productWithDetails,
                onClick = onItemClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

enum class ProductStockStatus(
    val label: String,
    val icon: ImageVector
) {
    OUT_OF_STOCK("Agotado", Icons.Rounded.Block),
    LOW_STOCK("Stock Bajo", Icons.Rounded.WarningAmber),
    IN_STOCK("En Stock", Icons.Rounded.CheckCircle);

    companion object {
        fun fromStock(stock: Double, minStock: Double): ProductStockStatus {
            return when {
                stock <= 0.0 -> OUT_OF_STOCK
                stock <= minStock -> LOW_STOCK
                else -> IN_STOCK
            }
        }
    }

    fun getColors(customColors: CustomColors): Pair<Color, Color> {
        return when (this) {
            OUT_OF_STOCK -> customColors.deleted to customColors.onDeleted
            LOW_STOCK -> customColors.inactiveContainer to customColors.onInactiveContainer
            IN_STOCK -> customColors.active to customColors.onActive
        }
    }
}

@Composable
fun ProductCard(
    productDetails: ProductWithDetailsModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val product = productDetails.product
    val unitType = productDetails.unitType
    val customColors = MaterialTheme.customColors

    val stockStatus = remember(product.stock, product.minStock) {
        ProductStockStatus.fromStock(product.stock, product.minStock)
    }

    val (chipBgColor, chipContentColor) = stockStatus.getColors(customColors)

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(productDetails.product.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Fila Superior: Solo Nombre y Chip de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.commercialName.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                // Chip de Estado de Stock
                Surface(
                    shape = CircleShape,
                    color = chipBgColor,
                    contentColor = chipContentColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = stockStatus.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stockStatus.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            if (unitType.name.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Text(
                        text = unitType.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Marca: ${product.brand.ifBlank { "Sin marca" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // Divisor
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Fila Inferior: Precios y Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Precio de venta",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Q%.2f".format(Locale.US, product.salePrice),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val stockFormatted = if (product.stock % 1.0 == 0.0) {
                    product.stock.toInt().toString()
                } else {
                    "%.2f".format(Locale.US, product.stock)
                }

                Text(
                    text = "Stock: $stockFormatted",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (product.stock <= 0.0) {
                        customColors.deleted
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
fun ServiceList(
    services: List<ServiceWithDetailsModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    var previousCount by remember { mutableIntStateOf(services.size) }
    val firstServiceId = services.firstOrNull()?.service?.id
    LaunchedEffect(services.size, firstServiceId) {
        if (services.size > previousCount && services.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = services.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(services, key = { it.service.id }) { serviceWithDetails ->
            ServiceItem(serviceWithDetails = serviceWithDetails)
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ServiceItem(
    serviceWithDetails: ServiceWithDetailsModel,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = serviceWithDetails.service.name.ifBlank { "Sin nombre de servicio" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SupplierList(
    modifier: Modifier = Modifier,
    suppliers: List<SupplierModel>,
    listState: LazyListState,
    onAction:(InventoryAction) -> Unit,
    onItemClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(suppliers.size) }
    val firstSupplierId = suppliers.firstOrNull()?.id
    LaunchedEffect(suppliers.size, firstSupplierId) {
        if (suppliers.size > previousCount && suppliers.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = suppliers.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(suppliers, key = { it.id }) { supplier ->
            SupplierItem(
                supplier = supplier,
                onCallClick = {
                    onAction(InventoryAction.OnCallClick(phoneNumber = it))
                },
                onWhatsAppClick = {
                    onAction(InventoryAction.OnWhatsappClick(phoneNumber = it))
                },
                onItemClick = { supplierId ->
                    onItemClick(supplierId)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SupplierItem(
    supplier: SupplierModel,
    onCallClick: (phoneNumber: String) -> Unit,
    onWhatsAppClick: (phoneNumber: String) -> Unit,
    modifier: Modifier = Modifier,
    onItemClick:(String) -> Unit
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            onItemClick(supplier.id)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Content Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular Icon Container (Avatar)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Details Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Name
                    Text(
                        text = supplier.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Phone Number
                    SupplierInfoRow(
                        icon = Icons.Outlined.Phone,
                        text = supplier.phoneNumber
                    )

                    // Tax ID (NIT)
                    if (supplier.taxId.isNotBlank()) {
                        SupplierInfoRow(
                            icon = Icons.Outlined.Badge,
                            text = "NIT: ${supplier.taxId}"
                        )
                    }

                    // Address
                    if (supplier.address.isNotBlank()) {
                        SupplierInfoRow(
                            icon = Icons.Outlined.LocationOn,
                            text = supplier.address
                        )
                    }

                    if (supplier.status == Constants.DELETED_STATUS){

                        Spacer(modifier = Modifier.height(8.dp))

                        StatusChipShort(
                            modifier = Modifier.align(Alignment.End),
                            status = supplier.status
                        )
                    }
                }
            }

            // Divider before bottom actions
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Bottom Actions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                // Call Action Button
                Row(
                    modifier = modifier
                        .weight(1f)
                        .clickable(onClick = { onCallClick(supplier.phoneNumber) })
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = "Llamar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Llamar",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Vertical Divider between buttons
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // WhatsApp Action Button
                Row(
                    modifier = modifier
                        .weight(1f)
                        .clickable(onClick = { onWhatsAppClick(supplier.phoneNumber) })
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.whatsapp),
                        contentDescription = "WhatsApp",
                        tint = MaterialTheme.customColors.whatsapp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WhatsApp",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.customColors.whatsapp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SupplierInfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EmptyProductsState(
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin productos registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes productos registrados en tu inventario.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onAddProductClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer producto")
        }
    }
}

@Composable
fun EmptyServicesState(
    modifier: Modifier = Modifier,
    onAddServiceClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin servicios registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes servicios registrados en el sistema.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onAddServiceClick
        ) {
            Icon(
                imageVector = Icons.Outlined.MedicalServices,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer servicio")
        }
    }
}

@Composable
fun EmptySuppliersState(
    modifier: Modifier = Modifier,
    onAddSupplierClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin proveedores registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes proveedores registrados en el sistema.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onAddSupplierClick
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer proveedor")
        }
    }
}

@PreviewLightDark
@Composable
private fun ItemSupplierPreview() {
    AttiTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            ProductCard(
                productDetails = ProductWithDetailsModel(
                    product = ProductModel(
                        id = "asdfasdfadsfa",
                        commercialName = "Comida para perros",
                        brand = "Dog Chow",
                        stock = 0.0,
                        minStock = 10.0,
                        salePrice = 38.00
                    ),
                    unitType = AppCatalogModel(
                        name = "Gramos"
                    )
                ),
                onClick = {}
            )
        }
    }
}