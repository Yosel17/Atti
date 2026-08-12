package yosel.dev.atti.screens.navigation_bar.inventory.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel

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
    onAction: (InventoryAction) -> Unit
) {
    val tabs = remember {
        listOf(
            InventoryTabData("Productos", Icons.Filled.Inventory2),
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
                                                listState = productListState
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
                                EmptyProductsState()
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
                                EmptyServicesState()
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
                                                listState = supplierListState
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
                                EmptySuppliersState()
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
    modifier: Modifier = Modifier
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
            ProductItem(productWithDetails = productWithDetails)
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProductItem(
    productWithDetails: ProductWithDetailsModel,
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
                text = productWithDetails.product.commercialName.ifBlank { "Sin nombre de producto" },
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
    suppliers: List<SupplierModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(suppliers, key = { it.id }) { supplier ->
            SupplierItem(supplier = supplier)
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SupplierItem(
    supplier: SupplierModel,
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
                text = supplier.name.ifBlank { "Sin nombre de proveedor" },
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
fun EmptyProductsState(
    modifier: Modifier = Modifier
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
                imageVector = Icons.Outlined.Inventory2,
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
    }
}

@Composable
fun EmptyServicesState(
    modifier: Modifier = Modifier
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
    }
}

@Composable
fun EmptySuppliersState(
    modifier: Modifier = Modifier
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
    }
}